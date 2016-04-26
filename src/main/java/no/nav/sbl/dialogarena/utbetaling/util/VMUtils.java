package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Trekk;
import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.YtelseVM;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.Component;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

public class VMUtils {

    /**
     * true hvis: <br>
     *     a) startDato og sluttDato ikke er null <br>
     *     b) startDato og sluttDato ikke er lik UnixEpoch <br>
     * @param startDato
     * @param sluttDato
     * @return
     */

    public static final String TILBAKEBETALING = "Tilbakebetaling ";

    public static boolean erGyldigStartSluttVerdier(DateTime startDato, DateTime sluttDato) {
        if(startDato == null || sluttDato == null) {
            return false;
        }
        final DateTime unixEpoch = new DateTime(0, ISOChronology.getInstance());
        return !(startDato.isEqual(unixEpoch) && sluttDato.isEqual(unixEpoch));
    }

    public static Transformer<Double, YtelseVM> skattTilYtelseVM(final Component component) {
        return skatt -> {
            if (skatt > 0) {
                return new YtelseVM(TILBAKEBETALING + new StringResourceModel("ytelse.skatt.beskrivelse.tekst", component, null).getString().toLowerCase(), skatt);
            }
            return new YtelseVM(new StringResourceModel("ytelse.skatt.beskrivelse.tekst", component, null).getString(), skatt);
        };
    }

    public static final Transformer<Trekk, YtelseVM> TREKK_TIL_YTELSE_VM = trekk -> {
        if (trekk.getTrekkBeloep() > 0) {
            return new YtelseVM(TILBAKEBETALING + trekk.getTrekksType().toLowerCase(), trekk.getTrekkBeloep());
        }
        return new YtelseVM(trekk.getTrekksType(), trekk.getTrekkBeloep());
    };

    public static final Transformer<Underytelse, YtelseVM> UNDERYTELSE_TIL_YTELSE_VM = underytelse -> new YtelseVM(
            underytelse.getYtelsesType(),
            underytelse.getYtelseBeloep(),
            underytelse.getSatsAntall(),
            underytelse.getSatsBeloep(),
            underytelse.getSatsType());
}
