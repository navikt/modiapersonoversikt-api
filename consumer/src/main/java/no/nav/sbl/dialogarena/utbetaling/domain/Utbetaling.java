package no.nav.sbl.dialogarena.utbetaling.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Utbetaling {

    private List<Bilag> bilag = new ArrayList<>();
    private String beskrivelse;
    private DateTime startDate;
    private DateTime endDate;
    private String periode;
    private String statuskode;
    private DateTime utbetalingsDato;
    private double bruttoBelop;
    private double nettoBelop;
    private String valuta;
    private String kontoNr;

    public Utbetaling(List<Bilag> bilag, String beskrivelse, String periode, String statuskode, DateTime utbetalingsDato, double bruttoBelop, double nettoBelop, String valuta, String kontoNr) {
        this.bilag = bilag;
        this.beskrivelse = beskrivelse;
        this.periode = periode;
        this.statuskode = statuskode;
        this.utbetalingsDato = utbetalingsDato;
        this.bruttoBelop = bruttoBelop;
        this.nettoBelop = nettoBelop;
        this.valuta = valuta;
        this.kontoNr = kontoNr;

        extractPeriodDates(periode);
    }

    public String getKontoNr() {
        return kontoNr;
    }

    public String getValuta() {
        return valuta;
    }

    public List<Bilag> getBilag() {
        return bilag;
    }

    public String getBeskrivelse() {
        return StringUtils.join(getBeskrivelser(), ", ");
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public String getPeriode() {
        return periode;
    }

    public String getStatuskode() {
        return statuskode;
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public double getBruttoBelop() {
        return bruttoBelop;
    }

    public double getNettoBelop() {
        return nettoBelop;
    }

    protected Set<String> getBeskrivelser() {
        Set<String> beskrivelser = new TreeSet<>();
        for (Bilag detalj : bilag) {
            beskrivelser.addAll(detalj.getBeskrivelser());
        }
        return beskrivelser;
    }

    private void extractPeriodDates(String periode) {
        // ÅÅÅÅ.MM.DD-ÅÅÅÅ.MM.DD
        if (periode != null) {
            String[] datoer = periode.split("-");
            if (datoer.length >= 1) {
                try {
                    startDate = DateTime.parse(datoer[0], DateTimeFormat.forPattern("YYYY.MM.dd"));
                } catch (IllegalArgumentException e) {
                    startDate = null;
                }
            }
            if (datoer.length >= 2) {
                try {
                    endDate = DateTime.parse(datoer[1], DateTimeFormat.forPattern("YYYY.MM.dd"));
                } catch (IllegalArgumentException e) {
                    endDate = null;
                }
            }
        } else {
            startDate = null;
            endDate = null;
        }
    }
}
