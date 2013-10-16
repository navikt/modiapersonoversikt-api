package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;

public class Melding implements Serializable {

    public final String overskrift,
                        sendtDato,
                        fritekst;

    public Melding(Meldingstype type, DateTime sendtDato, String fritekst) {
        this.overskrift = type == INNGAENDE ? "Melding fra bruker" : "Svar fra NAV";
        this.sendtDato = Datoformat.lang(sendtDato);
        this.fritekst = fritekst;
    }

    public CompoundPropertyModel<Boolean> tidligereHenvendelse = new CompoundPropertyModel<>(true);

}
