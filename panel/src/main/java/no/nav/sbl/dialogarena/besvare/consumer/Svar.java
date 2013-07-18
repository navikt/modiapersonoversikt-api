package no.nav.sbl.dialogarena.besvare.consumer;

import java.io.Serializable;

public class Svar implements Serializable {

    public String id, tema, fritekst, overskrift;
    public boolean sensitiv;

    public Svar withId(String id) {
        this.id = id;
        return this;
    }

    public Svar withTema(String tema) {
        this.tema = tema;
        return this;
    }

    public Svar withFritekst(String fritekst) {
        this.fritekst = fritekst;
        return this;
    }

    public Svar withOverskrift(String overskrift) {
        this.overskrift = overskrift;
        return this;
    }

    public Svar withSensitiv(boolean sensitiv) {
        this.sensitiv = sensitiv;
        return this;
    }
}
