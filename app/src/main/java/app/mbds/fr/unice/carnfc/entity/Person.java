package app.mbds.fr.unice.carnfc.entity;

import java.io.Serializable;

/**
 * Created by Léo-paul MARTIN
 */

public class Person implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Car[] cars;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "firstName:" + getFirstName() + " - lastName:" + getLastName() + " - email:" + getEmail() + " - password:" + getPassword();
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
