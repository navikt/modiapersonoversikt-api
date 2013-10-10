package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;

public class Henvendelse implements Serializable {
    private String overskrift, sendtDato, fritekst;

    public Henvendelse(Meldingstype type, DateTime sendtDato, String fritekst) {
        this.overskrift = "Fra " + (type == INNGAENDE ? "Bruker" : "NAV");
        this.sendtDato = Datoformat.lang(sendtDato);
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

    public CompoundPropertyModel<Boolean> tidligereHenvendelse = new CompoundPropertyModel<>(true);

}
