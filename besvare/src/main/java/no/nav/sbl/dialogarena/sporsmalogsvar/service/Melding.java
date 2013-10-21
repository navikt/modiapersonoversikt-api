package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.hash;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;

/**
 * En ferdigskrevet melding.
 */
public class Melding implements Serializable {

    public final String overskrift, fritekst;
    public final String behandlingId;
    public final DateTime sendtDato;

    public Melding(String behandlingId, Meldingstype type, DateTime sendtDato, String fritekst) {
        this.behandlingId = behandlingId;
        this.overskrift = type == INNGAENDE ? "Melding fra bruker" : "Svar fra NAV";
        this.sendtDato = sendtDato;
        this.fritekst = fritekst;
    }

    public String getSendtDato() {
        return Datoformat.lang(sendtDato);
    }

    public static final Transformer<Melding, DateTime> SENDT_DATO = new Transformer<Melding, DateTime>() {
        @Override public DateTime transform(Melding melding) {
            return melding.sendtDato;
        }
    };

    public static final Transformer<Melding, String> FRITEKST = new Transformer<Melding, String>() {
        @Override public String transform(Melding melding) {
            return melding.fritekst;
        }
    };

    public static final Transformer<Melding, String> BEHANDLING_ID = new Transformer<Melding, String>() {
        @Override public String transform(Melding melding) {
            return melding.behandlingId;
        }
    };

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Melding) {
            Melding other = (Melding) obj;
            return Objects.equals(this.overskrift, other.overskrift)
                && Objects.equals(this.fritekst, other.fritekst)
                && Objects.equals(this.sendtDato, other.sendtDato);
        }
        return false;
    };

    @Override
    public final int hashCode() {
        return hash(overskrift, fritekst, sendtDato);
    }

    public CompoundPropertyModel<Boolean> tidligereHenvendelse = new CompoundPropertyModel<>(true);

}
