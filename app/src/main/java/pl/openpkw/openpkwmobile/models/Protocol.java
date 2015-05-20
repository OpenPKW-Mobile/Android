package pl.openpkw.openpkwmobile.models;

import java.util.HashMap;
import java.util.Map;

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

    public Protocol(Map<String, Integer> results, String pkwId) {
        this.results = results;
        this.pkwId = pkwId;
    }

    private Map<String, Integer> results;
    private String pkwId;

    public Map<String, Integer> getResults() {
        return results;
    }

    public void setResults(Map<String, Integer> results) {
        this.results = results;
    }

    public String getPkwId() {
        return pkwId;
    }

    public void setPkwId(String pkwId) {
        this.pkwId = pkwId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Protocol)) return false;

        if (this.pkwId.equals(((Protocol) other).getPkwId())) {
            for (String k : this.results.keySet()) {
                Integer value = this.results.get(k);
                if (((Protocol) other).results.containsKey(k)) {
                    if (!((Protocol) other).results.get(k).equals(value)) {
                        return false;
                    }
                }
            }
            return true;
        } else
            return false;
    }
}
