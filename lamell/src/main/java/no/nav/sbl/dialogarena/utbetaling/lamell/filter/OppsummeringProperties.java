package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.logikk.OppsummeringsKalkulator;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.List;


public class OppsummeringProperties implements Serializable {

    private List<Utbetaling> utbetalinger;

    private LocalDate sluttDato;
    private LocalDate startDato;
    private Periode oppsummertPeriode;
    private Oppsummering oppsummering;

    public OppsummeringProperties(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        this.utbetalinger = utbetalinger;
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        oppsummertPeriode = createPeriode(startDato, sluttDato);
        oppsummering = regnUtOppsummering();
    }

    private Oppsummering regnUtOppsummering() {
        return OppsummeringsKalkulator.regnUtOppsummering(utbetalinger);
    }

    private Periode createPeriode(LocalDate start, LocalDate slutt) {
        return new Periode(start.toDateTimeAtStartOfDay(), slutt.toDateMidnight().toDateTime());
    }
}
