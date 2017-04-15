package app.mbds.fr.unice.carnfc.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import app.mbds.fr.unice.carnfc.CarNFCApplication;
import app.mbds.fr.unice.carnfc.R;
import app.mbds.fr.unice.carnfc.map.mode.ModeGeolocation;
import app.mbds.fr.unice.carnfc.map.mode.ModeSearchCar;
import app.mbds.fr.unice.carnfc.map.mode.ModeSearchParking;

/**
 * Created by 53js-Seb on 16/01/2017.
 */

public class MapGeolocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    //Mode
    private ModeGeolocation currentMode;
    private ModeSearchCar modeSearchCar;
    private ModeSearchParking modeSearchParking;

    private Context context;
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private Marker currentLoc;


    public MapGeolocation(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;

        //init mode
        modeSearchParking = new ModeSearchParking(context, googleMap);
        modeSearchCar = new ModeSearchCar(context, googleMap);

        //Init google api
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
    }

    public void start(){
        googleApiClient.connect();
    }

    public void stop(){
        googleApiClient.disconnect();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(context.getResources().getInteger(R.integer.geolocation_interval)); //10s
        locationRequest.setFastestInterval(context.getResources().getInteger(R.integer.geolocation_fastest_interval));//5s
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return locationRequest;
    }

    private boolean checkPermission(){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void startLocationUpdate() {
        if(!checkPermission()){
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, createLocationRequest(), this);
    }

    public void stopLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();

        if(!checkPermission()){
            return;
        }

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        updateLocationOnMap(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdate();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "On connection failed ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location loc) {
        updateLocationOnMap(loc);
    }

    private void updateLocationOnMap(Location loc){
        if(googleMap == null || loc == null){
            return;
        }

        if(currentLoc != null){
            currentLoc.remove();
        }

        MarkerOptions markerOpt = new MarkerOptions().position(
                new LatLng(loc.getLatitude(), loc.getLongitude()));
        currentLoc = googleMap.addMarker(markerOpt);
    }

    public void activeModeSearchCar(){
        if(currentMode != null){
            currentMode.clean();
        }

        currentMode = modeSearchCar;
        currentMode.updateGoogleMap();
    }

    public void activeModeSearchParking(){
        if(currentMode != null){
            currentMode.clean();
        }

        currentMode = modeSearchParking;
        currentMode.updateGoogleMap();
    }

}
