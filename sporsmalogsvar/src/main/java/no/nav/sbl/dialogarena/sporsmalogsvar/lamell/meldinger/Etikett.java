package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.meldinger;

import java.io.Serializable;

public class Etikett implements Serializable {

    private final String tekst;
    private final String cssKlasse;

    public Etikett(String tekst, String cssKlasse) {
        this.tekst = tekst;
        this.cssKlasse = cssKlasse;
    }

    public String getTekst() {
        return tekst;
    }

    public String getCssKlasse() {
        return cssKlasse;
    }

}
