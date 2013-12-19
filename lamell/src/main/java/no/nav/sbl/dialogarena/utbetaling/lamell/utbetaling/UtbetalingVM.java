package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;
import static org.apache.commons.lang3.StringUtils.join;

public class UtbetalingVM {

    public Utbetaling utbetaling;

    public UtbetalingVM(Utbetaling utbetaling) {
        this.utbetaling = utbetaling;
    }

    public String getBeskrivelse() {
        return join(utbetaling.getBeskrivelser(), ", ");
    }

    public String getKortUtbetalingsDato() {
        return optional(utbetaling.utbetalingsDato).map(KORT).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(utbetaling.startDato).map(KORT).getOrElse("") + " - " + optional(utbetaling.sluttDato).map(KORT).getOrElse("");
    }

    public String getBruttoBelopMedValuta() {
        return getBelopString(utbetaling.bruttoBelop);
    }

    public String getTrekkMedValuta() {
        return getBelopString(utbetaling.trekk);
    }

    public String getBelopMedValuta() {
        return getBelopString(utbetaling.nettoBelop);
    }

    public boolean harYtelse(String ytelse) {
        return utbetaling.getBeskrivelser().contains(ytelse);
    }
}
