package app.mbds.fr.unice.carnfc.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import app.mbds.fr.unice.carnfc.R;
import app.mbds.fr.unice.carnfc.entity.Car;

/**
 * Created by 53js-Seb on 15/04/2017.
 */

public class CarTask extends AsyncTask<Void, Void, String>{

    private static final String TAG = "CarTask";

    private Context context;
    private CarCallback carCallback;

    public CarTask(Context context, CarCallback carCallback) {
        this.context = context;
        this.carCallback = carCallback;
    }
    @Override
    protected String doInBackground(Void... params) {
        StringBuilder result = new StringBuilder();
        try {
            String server = context.getResources().getString(R.string.url_server);
            String service = context.getResources().getString(R.string.url_service_cars);

            URL url = new URL( server + service);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("GET");
            connection.connect();

            int codeResponse = connection.getResponseCode();
            if( 200 <= codeResponse && codeResponse < 300 ){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while((line = br.readLine()) != null){
                    result.append(line);
                }
                br.close();
            }

            //Close
            connection.disconnect();

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(result == null || result.isEmpty()) {
            Log.d(TAG, "Result is empty...");
            return;
        }

        try{
            //https://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/reflect/TypeToken.html
            Type listType = new TypeToken<ArrayList<Car>>() {}.getType();
            List<Car> list = new Gson().fromJson(result, listType);
            carCallback.onCarCallback(list);
        }catch(Exception e){
            Toast.makeText(context, R.string.error_parse_json, Toast.LENGTH_SHORT).show();
        }
    }
}
