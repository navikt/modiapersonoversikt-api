package no.nav.sbl.dialogarena.sak.viewdomain.lamell;

import java.io.Serializable;

public class Dokument implements Serializable {

    public String kodeverkRef;
    public String tilleggstittel;
    public boolean innsendt;
    public boolean hovedskjema;
    public String innsendingsvalg;

    public Dokument withKodeverkRef(String ref) {
        kodeverkRef = ref;
        return this;
    }

    public Dokument withTilleggsTittel(String tittel) {
        tilleggstittel = tittel;
        return this;
    }

    public Dokument withInnsendt(boolean erInnsendt) {
        innsendt = erInnsendt;
        return this;
    }

    public Dokument withHovedskjema(boolean erHovedskjema) {
        hovedskjema = erHovedskjema;
        return this;
    }

    public Dokument withInnsendingsvalg(String innsendingsvalg) {
        this.innsendingsvalg = innsendingsvalg;
        return this;
    }
}
