package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BEREGNET_TREKK;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUTTO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.NETTO;
import static no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse.HovedYtelseComparator.NAVN;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.summerBelopForUnderytelser;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.ValutaUtil.getBelopString;


public class OppsummeringVM implements Serializable {

    private List<Utbetaling> utbetalinger;
    private LocalDate sluttDato;
    private LocalDate startDato;
    public String utbetalt, trekk, brutto;
    public List<HovedYtelse> hovedytelser;

    public OppsummeringVM(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        this.utbetalinger = utbetalinger;
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        this.utbetalt = getBelopString(on(utbetalinger).map(NETTO).reduce(sumDouble));
        this.trekk = getBelopString(on(utbetalinger).map(BEREGNET_TREKK).reduce(sumDouble));
        this.brutto = getBelopString(on(utbetalinger).map(BRUTTO).reduce(sumDouble));
        this.hovedytelser = new ArrayList<>();
        Map<String,Map<String,Double>> ytelserUtbetalt = summerBelopForUnderytelser(utbetalinger);
        for (String hovedytelse : ytelserUtbetalt.keySet()) {
            hovedytelser.add(new HovedYtelse(hovedytelse, ytelserUtbetalt.get(hovedytelse)));
        }
        sort(hovedytelser, NAVN);
    }

    public List<Utbetaling> getUtbetalinger() {
        return utbetalinger;
    }

    public LocalDate getSluttDato() {
        return sluttDato;
    }

    public LocalDate getStartDato() {
        return startDato;
    }

    public String getOppsummertPeriode() {
        if (startDato.getMonthOfYear() == sluttDato.getMonthOfYear()) {
            return startDato.toString("MMMM", Locale.forLanguageTag("nb"));
        }
        return KORT.transform(startDato.toDateTimeAtStartOfDay()) + " - " + KORT.transform(sluttDato.toDateMidnight().toDateTime());
    }
}
