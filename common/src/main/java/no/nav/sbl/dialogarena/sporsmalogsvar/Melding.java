package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * En ferdigskrevet melding.
 */
public class Melding implements Serializable {

    public final String behandlingId,
                        avsender,
                        fritekst;

    public final DateTime sendtDato;

    public Melding(String behandlingId, Meldingstype type, DateTime sendtDato, String fritekst) {
        this.behandlingId = behandlingId;
        this.avsender = type == INNGAENDE ? "bruker" : "NAV";
        this.sendtDato = sendtDato;
        this.fritekst = fritekst;
    }

    public boolean innleder(Traad traad) {
        List<Melding> dialog = traad.getDialog();
        int dialogPos = dialog.indexOf(this);
        return dialogPos != -1 && dialogPos == dialog.size() - 1;
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
            return Objects.equals(this.avsender, other.avsender)
                && Objects.equals(this.behandlingId, other.behandlingId)
                && Objects.equals(this.fritekst, other.fritekst)
                && Objects.equals(this.sendtDato, other.sendtDato);
        }
        return false;
    };

    @Override
    public final int hashCode() {
        return hash(avsender, behandlingId, fritekst, sendtDato);
    }

    @Override
    public String toString() {
        return String.format("Melding (%s) fra %s: '%s'", behandlingId, avsender, abbreviate(fritekst, 30));
    }


    /**
     * @deprecated Eneste Wicket-relatert som er igjen i domenemodell, og bør fjernes.
     *             Er dette noe som meldingen/tråden kan resolve selv?
     */
    @Deprecated
    public CompoundPropertyModel<Boolean> tidligereHenvendelse = new CompoundPropertyModel<>(true);

}
