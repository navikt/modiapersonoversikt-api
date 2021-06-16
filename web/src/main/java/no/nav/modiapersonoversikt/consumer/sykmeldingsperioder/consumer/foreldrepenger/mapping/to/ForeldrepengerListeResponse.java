package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping.to;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger.Foreldrepengerettighet;

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
