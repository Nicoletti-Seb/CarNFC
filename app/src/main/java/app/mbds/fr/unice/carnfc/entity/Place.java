package app.mbds.fr.unice.carnfc.entity;

/**
 * Created by MBDS on 02/04/2017.
 */

public class Place {

    private float lat;
    private float lon;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "lat:"+getLat()+" - lon:"+getLon();
    }
}
