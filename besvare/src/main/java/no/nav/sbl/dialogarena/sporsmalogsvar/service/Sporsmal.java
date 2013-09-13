package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import org.joda.time.DateTime;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareUtils.formatertDato;

public class Sporsmal implements Serializable {

    private String fritekst;

    private DateTime sendtDato;

    public Sporsmal(String fritekst, DateTime sendtDato) {
        this.fritekst = fritekst;
        this.sendtDato = sendtDato;
    }

    public String getSendtDatoAsString() {
        return formatertDato(sendtDato);
    }

    public DateTime getSendtDato() {
        return sendtDato;
    }

    public String getFritekst() {
        return fritekst;
    }
}
