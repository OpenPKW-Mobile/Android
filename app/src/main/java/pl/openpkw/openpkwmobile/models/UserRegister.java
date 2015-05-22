package pl.openpkw.openpkwmobile.models;

import java.io.Serializable;

/**
 * Created by Wojciech Radzioch on 14.05.15.
 */

public class UserRegister implements Serializable {

    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
