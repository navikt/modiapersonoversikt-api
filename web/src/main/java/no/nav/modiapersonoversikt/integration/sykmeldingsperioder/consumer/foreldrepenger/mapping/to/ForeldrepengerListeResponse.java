package no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.foreldrepenger.mapping.to;

import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengerettighet;

import java.io.Serializable;

public class ForeldrepengerListeResponse implements Serializable {

    private Foreldrepengerettighet foreldrepengerettighet;

    public ForeldrepengerListeResponse() {
    }

    public ForeldrepengerListeResponse(Foreldrepengerettighet foreldrepengerettighetListe) {
        this.foreldrepengerettighet = foreldrepengerettighetListe;
    }

    public Foreldrepengerettighet getForeldrepengerettighet() {
        return foreldrepengerettighet;
    }

    public void setForeldrepengerettighet(Foreldrepengerettighet foreldrepengerettighet) {
        this.foreldrepengerettighet = foreldrepengerettighet;
    }
}
