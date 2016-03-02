package no.nav.sbl.dialogarena.saksoversikt.service.providerdomain;

import static java.lang.Boolean.*;

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
