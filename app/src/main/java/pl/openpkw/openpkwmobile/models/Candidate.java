package pl.openpkw.openpkwmobile.models;

/**
 * Created by michalu on 05.05.15.
 */
public class Candidate {
    /*
            "pkwId": 2,
            "firstname": "Bronisław Maria",
            "lastname": "Komorowski",
            "glosow": 0
     */
    private int pkwId;
    private String firstname;
    private String lastname;
    private int glosow;

    public int getPkwId() {
        return pkwId;
    }

    public void setPkwId(int pkwId) {
        this.pkwId = pkwId;
    }

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

    public int getGlosow() {
        return glosow;
    }

    public void setGlosow(int glosow) {
        this.glosow = glosow;
    }

    public String getFullName() {
        return this.getFirstname() + " " + getLastname();
    }
}
