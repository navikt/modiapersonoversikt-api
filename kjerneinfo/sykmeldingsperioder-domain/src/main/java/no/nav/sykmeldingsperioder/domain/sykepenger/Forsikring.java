package no.nav.sykmeldingsperioder.domain.sykepenger;

import no.nav.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class Forsikring implements Serializable {
    private String forsikringsordning;
    private Double premiegrunnlag;
    private boolean erGyldig;
    private Periode forsikret;

    public Forsikring() {
    }

    public Forsikring(boolean erGyldig, String forsikringsordning, Periode forsikret, Double premiegrunnlag) {
        this.erGyldig = erGyldig;
        this.forsikringsordning = forsikringsordning;
        this.forsikret = forsikret;
        this.premiegrunnlag = premiegrunnlag;
    }

    public boolean getErGyldig() {
        return erGyldig;
    }

    public void setErGyldig(boolean erGyldig) {
        this.erGyldig = erGyldig;
    }

    public String getForsikringsordning() {
        return forsikringsordning;
    }

    public void setForsikringsordning(String forsikringsordning) {
        this.forsikringsordning = forsikringsordning;
    }

    public Periode getForsikret() {
        return forsikret;
    }

    public void setForsikret(Periode forsikret) {
        this.forsikret = forsikret;
    }

    public Double getPremiegrunnlag() {
        return premiegrunnlag;
    }

    public void setPremiegrunnlag(Double premiegrunnlag) {
        this.premiegrunnlag = premiegrunnlag;
    }
}
