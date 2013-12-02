package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.joda.time.DateTime.parse;
import static org.joda.time.format.DateTimeFormat.forPattern;


public class Periode implements Serializable {

    public static final String DELIMITER = "-";
    private DateTime startDato;
    private DateTime sluttDato;
    private String periodeString;
    private Interval interval;

    public Periode(DateTime periodeFomDato, DateTime periodeTomDato) {
        init(periodeFomDato, periodeTomDato);
    }

    public Periode(LocalDate startDato, LocalDate sluttDato) {
        init(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime());
    }

    public Periode(WSPeriode utbetalingsPeriode) {
        if (utbetalingsPeriode == null) {
            return;
        }
        init(utbetalingsPeriode.getPeriodeFomDato(), utbetalingsPeriode.getPeriodeTomDato());
    }

    public Periode(String periodeString) {
        extractPeriodDates(periodeString, DELIMITER);
    }

    private void init(DateTime periodeFomDato, DateTime periodeTomDato) {
        this.startDato = periodeFomDato;
        this.sluttDato = periodeTomDato;
        this.periodeString = setPeriode(periodeFomDato, periodeTomDato, DELIMITER);
        this.interval = new Interval(startDato.getMillis(), sluttDato.getMillis());
    }

    public String getPeriodeString(Transformer<DateTime, String> datoFormat) {
        return datoFormat.transform(startDato) + " " + DELIMITER + " " + datoFormat.transform(sluttDato);
    }

    public boolean containsDate(DateTime date) {
        return interval.contains(date) || startDato.equals(date) || sluttDato.equals(date);
    }

    public String getPeriode() {
        return periodeString;
    }

    public DateTime getSluttDato() {
        return sluttDato;
    }

    public DateTime getStartDato() {
        return startDato;
    }

    @Override
    public String toString() {
        return periodeString;
    }

    private String setPeriode(DateTime periodeFomDato, DateTime periodeTomDato, String delimiter) {
        // ÅÅÅÅ.MM.DD-ÅÅÅÅ.MM.DD
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date fraDato = periodeFomDato.toDate();
        Date tilDato = periodeTomDato.toDate();
        return format.format(fraDato) + delimiter + format.format(tilDato);
    }

    private void extractPeriodDates(String periode, String delimiter) {
        // ÅÅÅÅ.MM.DD-ÅÅÅÅ.MM.DD
        if (periode != null) {
            String[] datoer = periode.split(delimiter);
            if (datoer.length >= 1) {
                try {
                    startDato = parse(datoer[0], forPattern("YYYY.MM.dd"));
                } catch (IllegalArgumentException e) {
                    startDato = null;
                }
            }
            if (datoer.length >= 2) {
                try {
                    sluttDato = parse(datoer[1], forPattern("YYYY.MM.dd"));
                } catch (IllegalArgumentException e) {
                    sluttDato = null;
                }
            }
        } else {
            startDato = null;
            sluttDato = null;
        }
    }
}
