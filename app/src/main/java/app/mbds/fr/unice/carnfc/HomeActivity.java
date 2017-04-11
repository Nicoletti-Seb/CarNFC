package app.mbds.fr.unice.carnfc;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import app.mbds.fr.unice.carnfc.map.MapGeolocalisation;

public class HomeActivity extends Activity implements OnMapReadyCallback{

    private static final String TAG = "HomeActivity";

    private MapGeolocalisation mapGeolocalisation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Init map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!checkPermission()) {
            Log.i(TAG, "Permission location denied...");
            return;
        }

        mapGeolocalisation = new MapGeolocalisation(this, googleMap);
        mapGeolocalisation.start();
    }


    private boolean checkPermission(){
        return  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mapGeolocalisation != null){
            mapGeolocalisation.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mapGeolocalisation != null){
            mapGeolocalisation.stop();
        }
    }
}
