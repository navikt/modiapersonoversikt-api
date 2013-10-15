package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.util.List;

public class Utbetaling {

    private List<Bilag> bilag;
    private String beskrivelse;
    private DateTime startDate;
    private DateTime endDate;
    private String periode;
    private String statuskode;
    private DateTime utbetalingsDato;
    private double bruttoBelop;
    private double nettoBelop;
    private String valuta;

    public Utbetaling(List<Bilag> bilag, String beskrivelse, DateTime startDate, DateTime endDate, String periode, String statuskode, DateTime utbetalingsDato, double bruttoBelop, double nettoBelop, String valuta) {
        this.bilag = bilag;
        this.beskrivelse = beskrivelse;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periode = periode;
        this.statuskode = statuskode;
        this.utbetalingsDato = utbetalingsDato;
        this.bruttoBelop = bruttoBelop;
        this.nettoBelop = nettoBelop;
        this.valuta = valuta;
    }

    public String getValuta() {
        return valuta;
    }

    public List<Bilag> getBilag() {
        return bilag;
    }

    public String getBeskrivelse() {
        return beskrivelse;
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


}
