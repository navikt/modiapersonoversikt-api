package no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FeilWrapper {
    private Boolean inneholderFeil = FALSE;
    private Feilmelding feilmelding;

    public FeilWrapper() { }

    public FeilWrapper(Feilmelding feilmelding) {
        this.inneholderFeil = TRUE;
        this.feilmelding = feilmelding;
    }


    public Boolean getInneholderFeil() {
        return inneholderFeil;
    }

    public Feilmelding getFeilmelding() {
        return feilmelding;
    }
}
