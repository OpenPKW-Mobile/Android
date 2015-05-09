package pl.openpkw.openpkwmobile.models;

import java.io.Serializable;

/**
 * Created by kuczmysz on 02.05.2015.
 */
public class Commission implements Serializable {

    private int id;
    private String pkwId;
    private String name;
    private String commissionCity;
    private String commissionNumber;
    private String address;
    private int protokolCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getProtokolCount() {
        return protokolCount;
    }

    public void setProtokolCount(int protokolCount) {
        this.protokolCount = protokolCount;
    }

    public String getCommissionCity() {
        return commissionCity;
    }

    public void setCommissionCity(String commissionCity) {
        this.commissionCity = commissionCity;
    }

    public String getCommissionNumber() {
        return commissionNumber;
    }

    public void setCommissionNumber(String commissionNumber) {
        this.commissionNumber = commissionNumber;
    }
}