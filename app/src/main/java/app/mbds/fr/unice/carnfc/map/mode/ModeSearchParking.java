package app.mbds.fr.unice.carnfc.map.mode;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import app.mbds.fr.unice.carnfc.R;
import app.mbds.fr.unice.carnfc.entity.Place;
import app.mbds.fr.unice.carnfc.service.PlaceCallback;
import app.mbds.fr.unice.carnfc.service.PlaceTask;

/**
 * Created by 53js-Seb on 15/04/2017.
 */

public class ModeSearchParking implements ModeGeolocation, PlaceCallback {

    private List<Marker> markers = new ArrayList<>();
    private GoogleMap googleMap;
    private Context context;

    public ModeSearchParking(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
    }

    @Override
    public void updateGoogleMap() {
        new PlaceTask(context, this).execute();
    }

    @Override
    public void clean() {
        for(Marker marker : markers){
            marker.remove();
        }
        markers.clear();
    }

    @Override
    public void onPlaceCallback(List<Place> places) {
        for(Place place : places){
            MarkerOptions markerOpt = new MarkerOptions().position(
                    new LatLng(place.getLat(), place.getLon()));
            markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_parking_black_24dp));
            markers.add(googleMap.addMarker(markerOpt));
        }
    }
}
