package pl.openpkw.openpkwmobile.models;

/**
 * Created by michalu on 05.05.15.
 */
public class Protocol {
    /*
     * {
     "glosowWaznych":100,
     "glosujacych":10,
     "k1":12,
     "k2":3,
     "k3":13,
     "k4":12,
     "k5":16,
     "k6":12,
     "k7":18,
     "k8":15,
     "k9":18,
     "k10":19,
     "k11":15,
     "glosowNieWaznych":17,
     "kartWaznych":11,
     "uprawnionych":661
     }
     */
    private int glosowWaznych;
    private int glosujacych;
    private int glosowNieWaznych;
    private int kartWaznych;
    private int uprawnionych;

    public int getGlosowWaznych() {
        return glosowWaznych;
    }

    public void setGlosowWaznych(int glosowWaznych) {
        this.glosowWaznych = glosowWaznych;
    }

    public int getGlosujacych() {
        return glosujacych;
    }

    public void setGlosujacych(int glosujacych) {
        this.glosujacych = glosujacych;
    }

    public int getGlosowNieWaznych() {
        return glosowNieWaznych;
    }

    public void setGlosowNieWaznych(int glosowNieWaznych) {
        this.glosowNieWaznych = glosowNieWaznych;
    }

    public int getKartWaznych() {
        return kartWaznych;
    }

    public void setKartWaznych(int kartWaznych) {
        this.kartWaznych = kartWaznych;
    }

    public int getUprawnionych() {
        return uprawnionych;
    }

    public void setUprawnionych(int uprawnionych) {
        this.uprawnionych = uprawnionych;
    }
}
