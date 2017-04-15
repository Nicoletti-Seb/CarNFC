package app.mbds.fr.unice.carnfc;

import android.app.Application;
import android.location.Location;

import app.mbds.fr.unice.carnfc.entity.Person;

/**
 * Created by 53js-Seb on 05/01/2017.
 */

public class CarNFCApplication extends Application {

    private Location location;

    private Person person;

    public synchronized void setLocation(Location location) {
        this.location = location;
    }

    public synchronized Location getLocation() {
        return location;
    }

    public synchronized void setPerson(Person person) {
        this.person = person;
    }

    public synchronized Person getPerson() {
        return person;
    }
}
