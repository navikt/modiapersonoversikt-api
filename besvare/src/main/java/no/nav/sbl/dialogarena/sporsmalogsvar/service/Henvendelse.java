package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import org.joda.time.DateTime;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.formatertDato;
import static no.nav.sbl.dialogarena.sporsmalogsvar.service.Henvendelsestype.SPORSMAL;

public class Henvendelse implements Serializable {
    private String overskrift, sendtDato, fritekst;

    public Henvendelse(Henvendelsestype type, DateTime sendtDato, String fritekst) {
        this.overskrift = "Fra " + (type == SPORSMAL ? "bruker" : "NAV");
        this.sendtDato = formatertDato(sendtDato);
        this.fritekst = fritekst;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public String getSendtDato() {
        return sendtDato;
    }

    public String getFritekst() {
        return fritekst;
    }
}
