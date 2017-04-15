package app.mbds.fr.unice.carnfc.service;

import java.util.List;

import app.mbds.fr.unice.carnfc.entity.Place;

/**
 * Created by 53js-Seb on 15/04/2017.
 */

public interface PlaceCallback {
    void onPlaceCallback(List<Place> places);
}
