package pl.openpkw.openpkwmobile.models;

/**
 * Created by michalu on 05.05.15.
 */
public class Constituency {
    /*
        "pkwId": "14",
        "name": "Okręgowa Komisja Wyborcza Nr 14 w Łodzi"
     */
    private String pkwId;
    private String name;

    public String getPkwId() {
        return pkwId;
    }

    public void setPkwId(String pkwId) {
        this.pkwId = pkwId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
