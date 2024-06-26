package no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.foreldrepenger.Foreldrepengerettighet;

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
