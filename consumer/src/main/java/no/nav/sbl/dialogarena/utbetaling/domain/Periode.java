package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.virksomhet.okonomi.utbetaling.v2.WSPeriode;
import org.joda.time.DateTime;

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

    private String periode;

    public Periode(DateTime periodeFomDato, DateTime periodeTomDato) {
        this.startDato = periodeFomDato;
        this.sluttDato = periodeTomDato;
        this.periode = setPeriode(periodeFomDato, periodeTomDato, DELIMITER);
    }

    public Periode(WSPeriode utbetalingsPeriode) {
        if (utbetalingsPeriode == null) {
            return;
        }
        this.startDato = utbetalingsPeriode.getPeriodeFomDato();
        this.sluttDato = utbetalingsPeriode.getPeriodeTomDato();
    }

    public Periode(String periodeString) {
        extractPeriodDates(periodeString, DELIMITER);
    }

    public String getPeriode() {
        return periode;
    }

    public DateTime getSluttDato() {
        return sluttDato;
    }

    public DateTime getStartDato() {
        return startDato;
    }

    @Override
    public String toString() {
       return periode;
    }

    private String setPeriode(DateTime periodeFomDato, DateTime periodeTomDato, String delimiter) {
        // ÅÅÅÅ.MM.DD-ÅÅÅÅ.MM.DD
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date date = periodeFomDato.toDate();
        Date date1 = periodeTomDato.toDate();
        return format.format(date) + delimiter + format.format(date1);
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
