package app.mbds.fr.unice.carnfc.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import app.mbds.fr.unice.carnfc.R;
import app.mbds.fr.unice.carnfc.entity.Car;

/**
 * Created by 53js-Seb on 15/04/2017.
 */

public class CreateCarTask extends AsyncTask<Car, Void, Boolean>{

    private static final String TAG = "CreateMenuTask";

    private final Context context;

    public CreateCarTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Car... params) {
        //Verify params
        if( params.length <= 0 ){
            return null;
        }
        Car car  = params[0];

        //Post request
        Gson g = new Gson();
        String stringJson = g.toJson(car, Car.class);
        boolean result = false;
        try {
            URL url = new URL(context.getString(R.string.url_server) + context.getString(R.string.url_service_add_car));
            Log.i(TAG, "URL: " + url);
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
    protected void onPostExecute(Boolean bool) {
        if(bool){
            Log.i(TAG, "Car create");
        } else {
            Log.i(TAG, "Error: Car no create");
        }
    }
}
