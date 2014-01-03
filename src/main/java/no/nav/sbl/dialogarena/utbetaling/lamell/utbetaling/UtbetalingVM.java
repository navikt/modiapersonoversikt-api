package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UtbetalingVM {

    private Utbetaling utbetaling;

    public UtbetalingVM(Utbetaling utbetaling) {
        this.utbetaling = utbetaling;
    }

    public String getUtbetalingId() {
        return utbetaling.getUtbetalingId();
    }

    public String getStatus() {
        return utbetaling.getStatus();
    }

    public String getBeskrivelse() {
        return utbetaling.getHovedytelse();
    }

    public String getMottakerId() {
        return utbetaling.getMottakerId();
    }

    public String getKontonr() {
        return utbetaling.getKontonr();
    }

    public List<Underytelse> getUnderytelser() {
        return utbetaling.getUnderytelser();
    }

    public String getKortUtbetalingsDato() {
        return optional(utbetaling.getUtbetalingsdato()).map(KORT).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(utbetaling.getPeriode().getStart()).map(KORT).getOrElse("") + " - "
                + optional(utbetaling.getPeriode().getEnd()).map(KORT).getOrElse("");
    }

    public String getBruttoBelopMedValuta() {
        return getBelopString(utbetaling.getBrutto());
    }

    public String getTrekkMedValuta() {
        return getBelopString(utbetaling.getTrekk());
    }

    public String getBelopMedValuta() {
        return getBelopString(utbetaling.getUtbetalt());
    }

}
