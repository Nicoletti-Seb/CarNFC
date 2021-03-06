package app.mbds.fr.unice.carnfc;

import android.*;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import app.mbds.fr.unice.carnfc.entity.Car;
import app.mbds.fr.unice.carnfc.entity.Place;
import app.mbds.fr.unice.carnfc.service.CreateCarTask;
import app.mbds.fr.unice.carnfc.service.CreatePlaceTask;
import app.mbds.fr.unice.carnfc.service.ServiceCallback;

public class NFCActivity extends AppCompatActivity {

    private static final String TAG = "NFCActivity";

    private TextView nfcState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nfcreader);

        nfcState = (TextView) findViewById(R.id.nfc_state);
    }

    private void resolveIntent(final Intent intent){
        if(!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            return;
        }

        new NfcTask(intent).execute();
    }

    private String readTag(Intent intent){
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] messages;

        if (rawMsgs == null) {
            Toast.makeText(this, "Le tag est vide...", Toast.LENGTH_LONG).show();
            return null;
        }

        StringBuilder result = new StringBuilder();
        for(Parcelable rawMsg : rawMsgs){
            NdefMessage msg = (NdefMessage)rawMsg;
            for(NdefRecord record : msg.getRecords()){
                byte[] type = record.getType();

                if(Arrays.equals(type, NdefRecord.RTD_TEXT)){
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0063;

                    String data = null;
                    try {
                        data = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                    } catch (UnsupportedEncodingException e) {
                        data = "";
                    }
                    result.append(data);
                }
            }
        }

        return result.toString();
    }

    /**
     * Create Ndef message to write to a pasive tag or send by Beam
     * @param text
     * @param mimeType
     * @return
     */
    public NdefMessage createNdefMessage(String text, String mimeType) {
        NdefRecord record = createRecord("UTF-8", text);
        NdefMessage msg = new NdefMessage(record);
        return msg;
    }

    public NdefRecord createRecord(String charset, String message)
    {
        //Calculate data array
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
        byte[] textBytes = message.getBytes(Charset.forName(charset));
        byte[] data = new byte[1 + langBytes.length + textBytes.length];

        //fill data array
        data[0] = (byte) (langBytes.length); // status
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    public void writeTag(Intent intent, String message) {
        NdefMessage ndefMsg = createNdefMessage(message, "text/plain");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);

        try {
            ndef.connect();

            if(ndef != null ){
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Le tag NFC est verrouillé en écriture !", Toast.LENGTH_LONG).show();
                } else if (ndef.getMaxSize() < ndefMsg.getByteArrayLength()) {
                    Toast.makeText(this, "La capacité du tag NFC est insuffisante !", Toast.LENGTH_LONG).show();
                } else {
                    ndef.writeNdefMessage(ndefMsg);

                    ndef.close();
                }
            }else { //Format tag
                NdefFormatable format =	NdefFormatable.get(tag);
                if(format == null){
                    return;
                }

                format.connect();
                format.format(ndefMsg);
                format.close() ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Write Tag: IOException", Toast.LENGTH_LONG).show();
        } catch (FormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Write Tag: FormatException", Toast.LENGTH_LONG).show();
        }
    }

    //NFC Beam detect
    @Override
    public void onNewIntent(Intent intent) {
        resolveIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent.getActivity(this, 0, intent, 0);
        resolveIntent(getIntent());
    }


    enum ActionNFC {
        SAVE_LOCATION("SAVE_LOCATION"),
        OUT_LOCATION("OUT_LOCATION");

        private String action;

        ActionNFC(String action){
            this.action = action;
        }

        public String getAction(){
            return action;
        }
    }


    private class NfcTask extends AsyncTask<Void, String, Void> implements GoogleApiClient.ConnectionCallbacks{

        private String actionRead;
        private Intent intent;
        private GoogleApiClient googleApiClient;

        private ServiceCallback finishCallback = new ServiceCallback() {
            @Override
            public void onCallback() {
                onFinish();
            }
        };

        public NfcTask(Intent intent){
            this.intent = intent;
        }

        private boolean checkPermission(){
            return ActivityCompat.checkSelfPermission(NFCActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    ||
                    ActivityCompat.checkSelfPermission(NFCActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress("READING");
            actionRead = readTag(intent);

            if( ActionNFC.OUT_LOCATION.getAction().equals(actionRead) ){
                //Write the next action
                publishProgress("WRITING SAVE_LOCATION");
                writeTag(intent, ActionNFC.SAVE_LOCATION.getAction());

            } else {
                //Write the next action
                publishProgress("WRITING OUT_LOCATION");
                writeTag(intent, ActionNFC.OUT_LOCATION.getAction());
            }

            publishProgress("UPDATE SERVER");

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            nfcState.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Update Serveur
            googleApiClient = new GoogleApiClient.Builder(NFCActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
            googleApiClient.connect();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if(!checkPermission()){
                onFinish();
                return;
            }

            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            {
                Place place = new Place((float)location.getLatitude(), (float)location.getLongitude());
                if( ActionNFC.OUT_LOCATION.getAction().equals(actionRead) ){
                    new CreatePlaceTask(NFCActivity.this, finishCallback).execute(place);
                }else{
                    new CreateCarTask(NFCActivity.this, finishCallback).execute(new Car(place));
                }
            }


        }

        private void onFinish(){
            //Vibrate the phone
            Vibrator v = (Vibrator) NFCActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(NFCActivity.this.getResources().getInteger(R.integer.delay_vibrator));

            //Start home activity
            Intent intent = new Intent(NFCActivity.this, HomeActivity.class);
            startActivity(intent);

            //Close activity
            NFCActivity.this.finish();
        }

        @Override
        public void onConnectionSuspended(int i) {}

    }
}
