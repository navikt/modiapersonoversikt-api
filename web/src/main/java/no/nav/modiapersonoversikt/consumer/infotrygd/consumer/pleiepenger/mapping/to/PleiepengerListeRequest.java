package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.pleiepenger.mapping.to;

import no.nav.modiapersonoversikt.commondomain.Periode;

import java.io.Serializable;

public class PleiepengerListeRequest implements Serializable {

    public final String ident;
    private Periode pleiepengerettighetPeriode;

    public PleiepengerListeRequest(String ident) {
        this.ident = ident;
    }

    public PleiepengerListeRequest(String ident, Periode pleiepengerettighetPeriode) {
        this.ident = ident;
        this.pleiepengerettighetPeriode = pleiepengerettighetPeriode;
    }

}
