package app.mbds.fr.unice.carnfc.map.mode;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import app.mbds.fr.unice.carnfc.entity.Car;
import app.mbds.fr.unice.carnfc.entity.Place;
import app.mbds.fr.unice.carnfc.service.CarCallback;
import app.mbds.fr.unice.carnfc.service.CarTask;

/**
 * Created by 53js-Seb on 15/04/2017.
 */

public class ModeSearchCar implements  ModeGeolocation, CarCallback{

    private List<Marker> markers = new ArrayList<>();
    private GoogleMap googleMap;
    private Context context;

    public ModeSearchCar(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
    }

    @Override
    public void updateGoogleMap() {
        new CarTask(context, this).execute();
    }

    @Override
    public void clean() {
        for(Marker marker : markers){
            marker.remove();
        }
        markers.clear();
    }

    @Override
    public void onCarCallback(List<Car> cars) {
        for(Car car : cars){
            Place place = car.getPlace();
            MarkerOptions markerOpt = new MarkerOptions().position(
                    new LatLng(place.getLat(), place.getLon()));
             markers.add(googleMap.addMarker(markerOpt));
        }
    }
}
