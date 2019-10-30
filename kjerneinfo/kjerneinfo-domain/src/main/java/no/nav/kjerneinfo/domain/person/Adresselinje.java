package no.nav.kjerneinfo.domain.person;

import java.io.Serializable;

/**
 * Interface for å hente adresser formatert som 1 linje
 */
public abstract class Adresselinje implements Serializable {

    private Endringsinformasjon endringsinformasjon;

    public abstract String getAdresselinje();

    public Endringsinformasjon getEndringsinformasjon() {
        return endringsinformasjon;
    }

    public void setEndringsinformasjon(Endringsinformasjon endringsinformasjon) {
        this.endringsinformasjon = endringsinformasjon;
    }

}
