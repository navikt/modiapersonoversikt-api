package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class UtbetalingBuilder {

    private List<Bilag> bilag = new ArrayList<>();
    private String beskrivelse = "Dagpenger";
    private String periode = "2013.10.07-2013.11.07";
    private String statuskode = "10";
    private DateTime utbetalingsDato = DateTime.now().minus(1);
    private double bruttoBelop = 6000.0;
    private double nettoBelop = 4800.0;
    private String valuta = "kr";
    private String kontoNr = "1234 25 25814";

    public UtbetalingBuilder() {

        bilag.addAll(asList(
                new BilagBuilder().createBilag(),
                new BilagBuilder().createBilag(),
                new BilagBuilder().createBilag()
        ));
    }

    public UtbetalingBuilder setBilag(List<Bilag> bilag) {
        this.bilag = bilag;
        return this;
    }

    public UtbetalingBuilder setKontoNr(String kontoNr) {
        this.kontoNr = kontoNr;
        return this;
    }

    public UtbetalingBuilder setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
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
        return new Utbetaling(bilag, beskrivelse, periode, statuskode, utbetalingsDato, bruttoBelop, nettoBelop, valuta, kontoNr);
    }

}