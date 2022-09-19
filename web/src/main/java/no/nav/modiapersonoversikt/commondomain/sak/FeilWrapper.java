package no.nav.modiapersonoversikt.commondomain.sak;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FeilWrapper {
    private Boolean inneholderFeil = FALSE;
    private Feilmelding feilmelding;

    public FeilWrapper() { }

    public FeilWrapper(Feilmelding feilmelding) {
        inneholderFeil = TRUE;
        this.feilmelding = feilmelding;
    }

    public Boolean getInneholderFeil() {
        return inneholderFeil;
    }

    public Feilmelding getFeilmelding() {
        return feilmelding;
    }
}
