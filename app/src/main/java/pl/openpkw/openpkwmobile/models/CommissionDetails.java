package pl.openpkw.openpkwmobile.models;

import java.util.List;

/**
 * Created by michalu on 05.05.15.
 */
public class CommissionDetails {
/*
    "pkwId": "106101-1",
    "name": "Studio Consulting Sp. z o.o.",
    "address": "ul. Romanowska 55F, 91-174 Łódź",
    "okregowa": {
        "pkwId": "14",
        "name": "Okręgowa Komisja Wyborcza Nr 14 w Łodzi"
    },
    "kandydatList"
 */
    private String pkwId;
    private String name;
    private String address;
    private Constituency okregowa;
    private List<Candidate> kandydatList;

    public String getPkwId() {
        return pkwId;
    }

    public void setPkwId(String pkwId) {
        this.pkwId = pkwId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Constituency getOkregowa() {
        return okregowa;
    }

    public void setOkregowa(Constituency okregowa) {
        this.okregowa = okregowa;
    }

    public List<Candidate> getKandydatList() {
        return kandydatList;
    }

    public void setKandydatList(List<Candidate> kandydatList) {
        this.kandydatList = kandydatList;
    }
}
