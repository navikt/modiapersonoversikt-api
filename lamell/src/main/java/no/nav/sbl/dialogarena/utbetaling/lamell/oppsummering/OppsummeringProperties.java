package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.Oppsummering;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import static no.nav.sbl.dialogarena.time.Datoformat.KORT;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.OppsummeringsKalkulator.regnUtOppsummering;


public class OppsummeringProperties implements Serializable {

    private List<Utbetaling> utbetalinger;
    private LocalDate sluttDato;
    private LocalDate startDato;
    private Oppsummering oppsummering;

    public OppsummeringProperties(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        this.utbetalinger = utbetalinger;
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        oppsummering = regnUtOppsummering(utbetalinger);
    }

    public List<Utbetaling> getUtbetalinger() {
        return utbetalinger;
    }

    public Oppsummering getOppsummering() {
        return oppsummering;
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
