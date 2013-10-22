package no.nav.sbl.dialogarena.utbetaling.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Utbetaling implements Serializable {

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

    // CHECKSTYLE:OFF
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
    // CHECKSTYLE:ON

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

    // CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Utbetaling that = (Utbetaling) o;

        if (Double.compare(that.bruttoBelop, bruttoBelop) != 0) return false;
        if (Double.compare(that.nettoBelop, nettoBelop) != 0) return false;
        if (beskrivelse != null ? !beskrivelse.equals(that.beskrivelse) : that.beskrivelse != null) return false;
        if (bilag != null ? !bilag.equals(that.bilag) : that.bilag != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (kontoNr != null ? !kontoNr.equals(that.kontoNr) : that.kontoNr != null) return false;
        if (periode != null ? !periode.equals(that.periode) : that.periode != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (statuskode != null ? !statuskode.equals(that.statuskode) : that.statuskode != null) return false;
        if (utbetalingsDato != null ? !utbetalingsDato.equals(that.utbetalingsDato) : that.utbetalingsDato != null)
            return false;
        if (valuta != null ? !valuta.equals(that.valuta) : that.valuta != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = bilag != null ? bilag.hashCode() : 0;
        result = 31 * result + (beskrivelse != null ? beskrivelse.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (periode != null ? periode.hashCode() : 0);
        result = 31 * result + (statuskode != null ? statuskode.hashCode() : 0);
        result = 31 * result + (utbetalingsDato != null ? utbetalingsDato.hashCode() : 0);
        temp = bruttoBelop != +0.0d ? Double.doubleToLongBits(bruttoBelop) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = nettoBelop != +0.0d ? Double.doubleToLongBits(nettoBelop) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (valuta != null ? valuta.hashCode() : 0);
        result = 31 * result + (kontoNr != null ? kontoNr.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Utbetaling{" +
                "beskrivelse='" + beskrivelse + '\'' +
                ", periode='" + periode + '\'' +
                ", statuskode='" + statuskode + '\'' +
                ", bilag=" + bilag +
                ", nettoBelop=" + nettoBelop +
                '}';
    }
    // CHECKSTYLE:ON
}
