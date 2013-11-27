package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class UtbetalingBuilder {

    private List<Bilag> bilag = new ArrayList<>();
    private String statuskode = "10";
    private DateTime utbetalingsDato = DateTime.now().minus(1);
    private double bruttoBelop = 6000.0;
    private double nettoBelop = 4800.0;
    private String valuta = "kr";
    private String kontoNr = "1234 25 25814";
    private String utbetalingId = "1";
    private String fnr = "12345678978";
    private Mottaker mottaker = new Mottaker(Mottaker.BRUKER, "Per Frode Kjellsen");
    private Periode periode = new Periode(new DateTime().minusDays(32), new DateTime().minusDays(2));

    public UtbetalingBuilder() {
        bilag.addAll(asList(
                new BilagBuilder().createBilag(),
                new BilagBuilder().createBilag(),
                new BilagBuilder().createBilag()
        ));
    }

    public UtbetalingBuilder setFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public UtbetalingBuilder setMottaker(Mottaker mottaker) {
        this.mottaker = mottaker;
        return this;
    }

    public UtbetalingBuilder setBilag(List<Bilag> bilag) {
        this.bilag = bilag;
        return this;
    }

    public UtbetalingBuilder setKontoNr(String kontoNr) {
        this.kontoNr = kontoNr;
        return this;
    }

    public UtbetalingBuilder setPeriode(String periode) {
        this.periode = new Periode(periode);
        return this;
    }

    public UtbetalingBuilder setPeriode(Periode periode) {
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

    public UtbetalingBuilder setUtbetalingId(String value) {
        this.utbetalingId = value;
        return this;
    }

    public Utbetaling createUtbetaling() {
        return new Utbetaling(fnr, bilag, statuskode, utbetalingsDato, bruttoBelop, nettoBelop, valuta, kontoNr, utbetalingId, mottaker, periode);
    }

}