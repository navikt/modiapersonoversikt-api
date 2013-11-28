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
    private Periode periode;
    private Oppsummering oppsummering;

    public OppsummeringProperties(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        this.utbetalinger = utbetalinger;
        this.sluttDato = sluttDato;
        this.startDato = startDato;
        periode = createPeriode(startDato, sluttDato);
        oppsummering = regnUtOppsummering();
    }

    private Oppsummering regnUtOppsummering() {
        return OppsummeringsKalkulator.regnUtOppsummering(utbetalinger);
    }

    private Periode createPeriode(LocalDate start, LocalDate slutt) {
        return new Periode(start.toDateTimeAtStartOfDay(), slutt.toDateMidnight().toDateTime());
    }

    public Periode getPeriode() {
        return periode;
    }

    public double getUtbetalt() {
        return oppsummering.utbetalt;
    }

    public double getTrekk() {
        return oppsummering.trekk;
    }

    public double getBrutto() {
        return oppsummering.brutto;
    }

    public void setUtbetalt(double utbetalt) {
        oppsummering.utbetalt = utbetalt;
    }

    public void setTrekk(double trekk) {
        oppsummering.trekk = trekk;
    }

    public void setBrutto(double brutto) {
        oppsummering.brutto = brutto;
    }

    public List<Utbetaling> getUtbetalinger() {
        return utbetalinger;
    }

    public void setUtbetalinger(List<Utbetaling> utbetalinger) {
        this.utbetalinger = utbetalinger;
    }

    public void setSluttDato(LocalDate sluttDato) {
        this.sluttDato = sluttDato;
    }

    public void setStartDato(LocalDate startDato) {
        this.startDato = startDato;
    }

    public void setPeriode(Periode periode) {
        this.periode = periode;
    }


    @Override
    public String toString() {
        return "OppsummeringProperties{" +
                "utbetalinger.size =" + utbetalinger.size() +
                ", sluttDato=" + sluttDato +
                ", startDato=" + startDato +
                ", periode=" + periode +
                ", oppsummering=" + oppsummering +
                '}';
    }
}
