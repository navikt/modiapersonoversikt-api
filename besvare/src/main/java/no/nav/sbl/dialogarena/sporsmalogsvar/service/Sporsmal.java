package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.sbl.dialogarena.sporsmalogsvar.Datoformat;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sporsmal implements Serializable {

    public final String fritekst;
    public final DateTime sendtDato;

    public Sporsmal(String fritekst, DateTime sendtDato) {
        this.fritekst = fritekst;
        this.sendtDato = sendtDato;
    }

    public String getSendtDatoAsString() {
        return Datoformat.lang(sendtDato);
    }

}
