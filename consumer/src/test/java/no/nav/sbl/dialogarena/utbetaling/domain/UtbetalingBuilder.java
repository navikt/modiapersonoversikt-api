package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;

public class UtbetalingBuilder {

    private List<Bilag> bilag = new ArrayList<>();
    private DateTime utbetalingsDato = DateTime.now().minus(1);
    private double bruttoBelop = 6000.0;
    private double nettoBelop = 4800.0;
    private double trekk = 1200.0;
    private String valuta = "kr";
    private String kontoNr = "1234 25 25814";
    private String utbetalingId = "1";
    private String fnr = "12345678978";
    private String mottakernavn = "Per Frode Kjellsen";
    private String mottakertype = BRUKER;
    private DateTime startDato = new DateTime().minusDays(32);
    private DateTime sluttDato = new DateTime().minusDays(2);

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

    public UtbetalingBuilder setMottakernavn(String mottakernavn) {
        this.mottakernavn = mottakernavn;
        return this;
    }

    public UtbetalingBuilder setMottakertype(String mottakertype) {
        this.mottakertype = mottakertype;
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

    public UtbetalingBuilder setStartDato(DateTime startDato) {
        this.startDato = startDato;
        return this;
    }

    public UtbetalingBuilder setSluttDato(DateTime sluttDato) {
        this.sluttDato = sluttDato;
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

    public UtbetalingBuilder setTrekk(double trekk) {
        this.trekk = trekk;
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
        Utbetaling utbetaling = new UtbetalingBuilder().setUtbetalingsDato(utbetalingId).createUtbetaling();
        utbetaling.fnr = fnr;
        utbetaling.bilag = bilag;
        utbetaling.utbetalingsDato = utbetalingsDato;
        utbetaling.bruttoBelop = bruttoBelop;
        utbetaling.nettoBelop = nettoBelop;
        utbetaling.valuta = valuta;
        utbetaling.kontoNr = kontoNr;
        utbetaling.startDato = startDato;
        utbetaling.sluttDato = sluttDato;
        utbetaling.trekk = trekk;
        utbetaling.mottakernavn = mottakernavn;
        utbetaling.mottakertype = mottakertype;
        return utbetaling;
    }

}