package app.mbds.fr.unice.carnfc;

import android.app.PendingIntent;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

public class NFCActivity extends AppCompatActivity {

    private static final String TAG = "NFCActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);
    }

    private void resolveIntent(final Intent intent){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
                    Log.i(TAG, readTag(intent));
                    writeTag(intent, "coucou");
                    Log.i(TAG, readTag(intent));
                }
                return null;
            }
        }.execute();
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
        } catch (FormatException e) {
            e.printStackTrace();
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
}
