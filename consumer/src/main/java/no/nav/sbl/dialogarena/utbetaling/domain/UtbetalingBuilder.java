package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class UtbetalingBuilder {
    private List<Bilag> bilag = new ArrayList<>();
    private String beskrivelse = "Dagpenger";
    private DateTime startDate = DateTime.now().minusDays(5);
    private DateTime endDate = DateTime.now().minusDays(2);
    private String periode = "2013.10.07-2013.11.07";
    private String statuskode = "10";
    private DateTime utbetalingsDato = DateTime.now().minus(1);
    private double bruttoBelop = 6000.0;
    private double nettoBelop = 4800.0;
    private String valuta = "kr";

    public UtbetalingBuilder setBilag(List<Bilag> bilag) {
        this.bilag = bilag;
        return this;
    }

    public UtbetalingBuilder setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
        return this;
    }

    public UtbetalingBuilder setStartDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public UtbetalingBuilder setEndDate(DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public UtbetalingBuilder setPeriode(String periode) {
        this.periode = periode;
        return this;
    }

    public UtbetalingBuilder setStatuskode(String statuskode) {
        this.statuskode = statuskode;
        return this;
    }

    public UtbetalingBuilder setUtbetalingsDato(DateTime utbetalingsDato) {
        this.utbetalingsDato = utbetalingsDato;
        return this;
    }

    public UtbetalingBuilder setBruttoBelop(double bruttoBelop) {
        this.bruttoBelop = bruttoBelop;
        return this;
    }

    public UtbetalingBuilder setNettoBelop(double nettoBelop) {
        this.nettoBelop = nettoBelop;
        return this;
    }

    public UtbetalingBuilder setValuta(String valuta) {
        this.valuta = valuta;
        return this;
    }



    public Utbetaling createUtbetaling() {
        return new Utbetaling(bilag, beskrivelse, startDate, endDate, periode, statuskode, utbetalingsDato, bruttoBelop, nettoBelop, valuta);
    }
}