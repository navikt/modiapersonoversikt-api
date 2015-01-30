package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT_UTEN_LITERAL;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.Mottaktertype;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;

public class UtbetalingVM {

    private transient Record<Hovedytelse> ytelse;

    public UtbetalingVM(Record<Hovedytelse> ytelse) {
        this.ytelse = ytelse;
    }

    public String getUtbetalingId() {
        return ytelse.get(Hovedytelse.id).toString();
    }

    public String getStatus() {
        return ytelse.getStatus();
    }

    public String getBeskrivelse() {
        return ytelse.get(Hovedytelse.ytelse);
    }

    public String getMelding() {
        return ytelse.get(Hovedytelse.utbetalingsmelding);
    }

    public String getMottakerNavn() {
        return ytelse.get(Hovedytelse.utbetaltTil).get(Aktoer.navn);
    }

    public Mottakertype getMottakertype() {
        return ytelse.get(Hovedytelse.mottakertype);
    }

    public String getKontonr() {
        return ytelse.get(Hovedytelse.utbetaltTilKonto).get(Konto.kontonummer);
    }

    public List<Record<Underytelse>> getUnderytelser() {
        return ytelse.get(Hovedytelse.underytelseListe);
    }

    public String getKortUtbetalingsDato() {
        return optional(ytelse.get(Hovedytelse.utbetalingsDato)).map(KORT_UTEN_LITERAL).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(ytelse.getPeriode().getStart()).map(KORT_UTEN_LITERAL).getOrElse("") + " - "
                + optional(ytelse.getPeriode().getEnd()).map(KORT_UTEN_LITERAL).getOrElse("");
    }

    public String getBruttoBelopMedValuta() {
        return getBelopString(ytelse.getBrutto());
    }

    public String getTrekkMedValuta() {
        return getBelopString(ytelse.getTrekk());
    }

    public String getBelopMedValuta() {
        return getBelopString(ytelse.getUtbetalt());
    }

    public DateTime getStartDato() {
        return ytelse.getPeriode().getStart();
    }

    public DateTime getSluttDato() {
        return ytelse.getPeriode().getEnd();
    }
}
