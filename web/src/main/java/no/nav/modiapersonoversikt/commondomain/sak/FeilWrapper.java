package no.nav.modiapersonoversikt.commondomain.sak;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FeilWrapper {
    private final Boolean inneholderFeil = FALSE;
    private Feilmelding feilmelding;

    public FeilWrapper() { }

    public Boolean getInneholderFeil() {
        return inneholderFeil;
    }

    public Feilmelding getFeilmelding() {
        return feilmelding;
    }
}
