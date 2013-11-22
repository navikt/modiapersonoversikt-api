package no.nav.sbl.dialogarena.utbetaling.domain.builder;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
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
    private Mottaker mottaker = new Mottaker("1", "12", "Arbeidsgiver");
    private Periode periode = new Periode(new DateTime().minusDays(32), new DateTime().minusDays(2));

    public UtbetalingBuilder() {
        bilag.addAll(asList(
                new BilagBuilder().createBilag(),
                new BilagBuilder().createBilag(),
                new BilagBuilder().createBilag()
        ));
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
        return new Utbetaling(bilag, statuskode, utbetalingsDato, bruttoBelop, nettoBelop, valuta, kontoNr, utbetalingId, mottaker, periode);
    }

}