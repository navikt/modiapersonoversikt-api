package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT_UTEN_LITERAL;
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

    public String getMelding() {
        return utbetaling.getMelding();
    }

    public String getMottakerNavn() {
        return utbetaling.getMottakernavn();
    }

    public String getKontonr() {
        return utbetaling.getKontonr();
    }

    public List<Underytelse> getUnderytelser() {
        return utbetaling.getUnderytelser();
    }

    public String getKortUtbetalingsDato() {
        return optional(utbetaling.getUtbetalingsdato()).map(KORT_UTEN_LITERAL).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(utbetaling.getPeriode().getStart()).map(KORT_UTEN_LITERAL).getOrElse("") + " - "
                + optional(utbetaling.getPeriode().getEnd()).map(KORT_UTEN_LITERAL).getOrElse("");
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
