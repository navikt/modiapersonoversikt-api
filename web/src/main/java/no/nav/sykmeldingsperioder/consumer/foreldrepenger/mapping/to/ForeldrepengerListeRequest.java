package no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to;

import no.nav.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class ForeldrepengerListeRequest implements Serializable {

    private String ident;
    private Periode foreldrepengerettighetPeriode;

    public ForeldrepengerListeRequest() {
    }

    public ForeldrepengerListeRequest(String ident, Periode foreldrepengerettighetPeriode) {
        this.ident = ident;
        this.foreldrepengerettighetPeriode = foreldrepengerettighetPeriode;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String brukerId) {
        this.ident = brukerId;
    }

    public Periode getForeldrepengerettighetPeriode() {
        return foreldrepengerettighetPeriode;
    }

    public void setForeldrepengerettighetPeriode(Periode foreldrepengerettighetPeriode) {
        this.foreldrepengerettighetPeriode = foreldrepengerettighetPeriode;
    }
}
