package app.mbds.fr.unice.carnfc.service;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import app.mbds.fr.unice.carnfc.R;
import app.mbds.fr.unice.carnfc.RegisterActivity;
import app.mbds.fr.unice.carnfc.entity.Person;

/**
 * Created by MBDS on 13/04/2017.
 */

public class RegisterTask extends AsyncTask<Person, Void, Boolean> {

    private final RegisterActivity mRegisterActivity;

    public RegisterTask(RegisterActivity registerActivity) {
        mRegisterActivity = registerActivity;
    }

    @Override
    protected Boolean doInBackground(Person... params) {
        //Verify params
        if( params.length <= 0 ){
            return null;
        }
        Person person  = params[0];

        //Post request
        Gson g = new Gson();
        String stringJson = g.toJson(person);
        boolean result = false;
        try {
            String server = mRegisterActivity.getString(R.string.url_server);
            String service = mRegisterActivity.getString(R.string.url_service_register);
            URL url = new URL(server + service);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.connect();

            //Write data
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            osw.write(stringJson);
            osw.flush();
            osw.close();

            int codeResponse = connection.getResponseCode();
            if( 200 <= codeResponse && codeResponse < 300 ){
                result = true;
            }

            //Close
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return result;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog(true);
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        showProgressDialog(false);
        mRegisterActivity.finish();
        Toast.makeText(mRegisterActivity, R.string.inscription_ok, Toast.LENGTH_LONG).show();
    }


    private ProgressDialog progressDialog;
    public void showProgressDialog(boolean isVisible) {
        if (isVisible) {
            if(progressDialog==null) {
                progressDialog = new ProgressDialog(mRegisterActivity);
                progressDialog.setMessage(mRegisterActivity.getResources().getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        progressDialog = null;
                    }
                });
                progressDialog.show();
            }
        }
        else {
            if(progressDialog!=null) {
                progressDialog.dismiss();
            }
        }
    }
}
