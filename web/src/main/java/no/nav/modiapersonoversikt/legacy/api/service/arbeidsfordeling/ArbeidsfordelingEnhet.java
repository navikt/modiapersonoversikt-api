package no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling;

import java.io.Serializable;

public class ArbeidsfordelingEnhet implements Serializable {
    private String enhetNr;
    private String navn;

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setEnhetNr(String enhetnr) {
        enhetNr = enhetnr;
    }

    public String getNavn() {
        return navn;
    }

    public String getEnhetNr() {
        return enhetNr;
    }
}
