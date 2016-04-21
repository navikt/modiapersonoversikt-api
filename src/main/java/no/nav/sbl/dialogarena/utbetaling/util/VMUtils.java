package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.sbl.dialogarena.common.records.Record;
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

    public static final Transformer<Record<Trekk>, YtelseVM> TREKK_TIL_YTELSE_VM = trekk -> {
        if (trekk.get(Trekk.trekkBeloep) > 0) {
            return new YtelseVM(TILBAKEBETALING + trekk.get(Trekk.trekksType).toLowerCase(), trekk.get(Trekk.trekkBeloep));
        }
        return new YtelseVM(trekk.get(Trekk.trekksType), trekk.get(Trekk.trekkBeloep));
    };

    public static final Transformer<Record<Underytelse>, YtelseVM> UNDERYTELSE_TIL_YTELSE_VM = underytelse -> new YtelseVM(
            underytelse.get(Underytelse.ytelsesType),
            underytelse.get(Underytelse.ytelseBeloep),
            underytelse.get(Underytelse.satsAntall),
            underytelse.get(Underytelse.satsBeloep),
            underytelse.get(Underytelse.satsType));
}
