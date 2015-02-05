package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.*;
import org.joda.time.DateTime;

import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT_UTEN_LITERAL;
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
        return ytelse.get(Hovedytelse.utbetalingsstatus);
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
        if(ytelse.get(Hovedytelse.utbetaltTilKonto).get(Konto.kontonummer) != null) {
            return ytelse.get(Hovedytelse.utbetaltTilKonto).get(Konto.kontonummer);
        }
        return ytelse.get(Hovedytelse.utbetaltTilKonto).get(Konto.kontotype);
    }

    public List<Record<Underytelse>> getUnderytelser() {
        return ytelse.get(Hovedytelse.underytelseListe);
    }

    public List<Record<Trekk>> getTrekkListe() {
        return ytelse.get(Hovedytelse.trekkListe);
    }

    public List<Double> getSkatteTrekk() {
        return ytelse.get(Hovedytelse.skattListe);
    }

    public String getKortPosteringsDato() {
        return optional(ytelse.get(Hovedytelse.posteringsdato)).map(KORT_UTEN_LITERAL).getOrElse("Ingen utbetalingsdato");
    }

    public String getPeriodeMedKortDato() {
        return optional(ytelse.get(Hovedytelse.ytelsesperiode).getStart()).map(KORT_UTEN_LITERAL).getOrElse("") + " - "
                + optional(ytelse.get(Hovedytelse.ytelsesperiode).getEnd()).map(KORT_UTEN_LITERAL).getOrElse("");
    }

    public String getBruttoBelop() {
        return getBelopString(ytelse.get(Hovedytelse.aggregertBruttoBeloep));
    }

    public String getTrekk() {
        return getBelopString(ytelse.get(Hovedytelse.aggregertTrekkBeloep));
    }

    public String getBelop() {
        return getBelopString(ytelse.get(Hovedytelse.ytelseNettoBeloep));
    }

    public DateTime getStartDato() {
        return ytelse.get(Hovedytelse.ytelsesperiode).getStart();
    }

    public DateTime getSluttDato() {
        return ytelse.get(Hovedytelse.ytelsesperiode).getEnd();
    }
}
