package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import java.io.Serializable;

public class ValgtMeldingOppdatert implements Serializable {
    public MeldingVM forrige, valgt;

    public boolean scroll;

    public ValgtMeldingOppdatert(MeldingVM forrige, MeldingVM valgt, boolean scroll) {
        this.forrige = forrige;
        this.valgt = valgt;
        this.scroll = scroll;
    }
}
