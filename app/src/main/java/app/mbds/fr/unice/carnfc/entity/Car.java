package app.mbds.fr.unice.carnfc.entity;

import java.util.Date;

/**
 * Created by MBDS on 02/04/2017.
 */

public class Car {

    private Person person;
    private String matricule = "";
    private String marque = "";
    private Date nextMaintenance;
    private Date endInsurance;
    private Place place;

    public Car(){}
    public Car(Place place){
        this.place = place;
        this.nextMaintenance = new Date();
        this.endInsurance = new Date();
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public Date getNextMaintenance() {
        return nextMaintenance;
    }

    public void setNextMaintenance(Date nextMaintenance) {
        this.nextMaintenance = nextMaintenance;
    }

    public Date getEndInsurance() {
        return endInsurance;
    }

    public void setEndInsurance(Date endInsurance) {
        this.endInsurance = endInsurance;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "matricule:"+getMatricule()+" - marque:"+getMarque()+" - nextMaintenance:"+getNextMaintenance()+" - endInsurance:"+getEndInsurance() + " - place:"+getPlace().toString();
    }
}
