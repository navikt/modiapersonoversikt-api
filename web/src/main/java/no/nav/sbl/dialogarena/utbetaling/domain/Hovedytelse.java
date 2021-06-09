package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("all")
public class Hovedytelse implements Serializable {

    String id;

    DateTime forfallsdato;
    DateTime utbetalingsDato;
    DateTime posteringsDato;
    Aktoer utbetaltTil;
    Mottakertype mottakertype;
    String utbetalingsmelding;
    String utbetaltTilKonto;
    String utbetalingsmetode;
    String utbetalingsstatus;

    String ytelse;
    Interval ytelsesperiode;
    List<Underytelse> underytelseListe;
    List<Trekk> trekkListe;
    Double sumTrekk;
    List<Double> skattListe;
    Double sumSkatt;
    Double nettoUtbetalt;
    String bilagsnummer;
    Aktoer rettighetshaver;
    Aktoer refundertForOrg;

    DateTime hovedytelsedato;
    Double bruttoUtbetalt;
    Double sammenlagtTrekkBeloep;
    boolean erHovedUtbetaling;

    public Double aggregateTrekkBeloep() {
        Double trekk = this.getSumTrekk();
        Double skatt = this.getSumSkatt();

        if (trekk == null) {
            trekk = 0.0;
        }

        if (skatt == null) {
            skatt = 0.0;
        }

        return trekk + skatt;
    }


    public String getId() {
        return id;
    }

    public DateTime getForfallsdato() {
        return forfallsdato;
    }

    public DateTime getUtbetalingsDato() {
        return utbetalingsDato;
    }

    public DateTime getPosteringsDato() {
        return posteringsDato;
    }

    public Aktoer getUtbetaltTil() {
        return utbetaltTil;
    }

    public Mottakertype getMottakertype() {
        return mottakertype;
    }

    public String getUtbetalingsmelding() {
        return utbetalingsmelding;
    }

    public String getUtbetaltTilKonto() {
        return utbetaltTilKonto;
    }

    public String getUtbetalingsmetode() {
        return utbetalingsmetode;
    }

    public String getUtbetalingsstatus() {
        return utbetalingsstatus;
    }

    public String getYtelse() {
        return ytelse;
    }

    public Interval getYtelsesperiode() {
        return ytelsesperiode;
    }

    public List<Underytelse> getUnderytelseListe() {
        return underytelseListe;
    }

    public List<Trekk> getTrekkListe() {
        return trekkListe;
    }

    public Double getSumTrekk() {
        return sumTrekk;
    }

    public List<Double> getSkattListe() {
        return skattListe;
    }

    public Double getSumSkatt() {
        return sumSkatt;
    }

    public Double getNettoUtbetalt() {
        return nettoUtbetalt;
    }

    public String getBilagsnummer() {
        return bilagsnummer;
    }

    public Aktoer getRettighetshaver() {
        return rettighetshaver;
    }

    public Aktoer getRefundertForOrg() {
        return refundertForOrg;
    }

    public DateTime getHovedytelsedato() {
        return hovedytelsedato;
    }

    public Double getBruttoUtbetalt() {
        return bruttoUtbetalt;
    }

    public Double getSammenlagtTrekkBeloep() {
        return sammenlagtTrekkBeloep;
    }

    public boolean isErHovedUtbetaling() {
        return erHovedUtbetaling;
    }

    public Hovedytelse withMottakertype(Mottakertype mottakertype) {
        this.mottakertype = mottakertype;
        return this;
    }

    public Hovedytelse withHovedytelsedato(DateTime hovedytelsesdato) {
        this.hovedytelsedato = hovedytelsesdato;
        return this;
    }

    public Hovedytelse withForfallsdato(DateTime forfallsdato) {
        this.forfallsdato = forfallsdato;
        return this;
    }

    public Hovedytelse withUtbetalingsDato(DateTime utbetalingsdato) {
        this.utbetalingsDato = utbetalingsdato;
        return this;
    }

    public Hovedytelse withPosteringsDato(DateTime posteringsdato) {
        this.posteringsDato = posteringsdato;
        return this;
    }

    public Hovedytelse withUtbetaltTil(Aktoer aktoer) {
        this.utbetaltTil = aktoer;
        return this;
    }

    public Hovedytelse withUtbetalingsmelding(String utbetalingsmelding) {
        this.utbetalingsmelding = utbetalingsmelding;
        return this;
    }

    public Hovedytelse withUtbetaltTilKonto(String kontoUtbetaltTil) {
        this.utbetaltTilKonto = kontoUtbetaltTil;
        return this;
    }

    public Hovedytelse withUtbetalingsmetode(String utbetalingsmetode) {
        this.utbetalingsmetode = utbetalingsmetode;
        return this;
    }

    public Hovedytelse withUtbetalingsstatus(String utbetalingsstatus) {
        this.utbetalingsstatus = utbetalingsstatus;
        return this;
    }

    public Hovedytelse withId(String id) {
        this.id = id;
        return this;
    }

    public Hovedytelse withYtelse(String ytelse) {
        this.ytelse = ytelse;
        return this;
    }

    public Hovedytelse withYtelsesperiode(Interval ytelsesperiode) {
        this.ytelsesperiode = ytelsesperiode;
        return this;
    }

    public Hovedytelse withUnderytelseListe(List<Underytelse> underytelser) {
        this.underytelseListe = underytelser;
        return this;
    }

    public Hovedytelse withTrekkListe(List<Trekk> trekkliste) {
        this.trekkListe = trekkliste;
        return this;
    }

    public Hovedytelse withSumTrekk(double trekksum) {
        this.sumTrekk = trekksum;
        return this;
    }

    public Hovedytelse withSkattListe(List<Double> skatteListe) {
        this.skattListe = skatteListe;
        return this;
    }

    public Hovedytelse withSumSkatt(double skattsum) {
        this.sumSkatt = skattsum;
        return this;
    }

    public Hovedytelse withNettoUtbetalt(double ytelseNettobeloep) {
        this.nettoUtbetalt = ytelseNettobeloep;
        return this;
    }

    public Hovedytelse withBilagsnummer(String bilagsnummer) {
        this.bilagsnummer = bilagsnummer;
        return this;
    }

    public Hovedytelse withRettighetshaver(Aktoer aktoer) {
        this.rettighetshaver = aktoer;
        return this;
    }

    public Hovedytelse withRefundertForOrg(Aktoer aktoer) {
        this.refundertForOrg = aktoer;
        return this;
    }

    public Hovedytelse withBruttoUtbetalt(double ytelseskomponentersum) {
        this.bruttoUtbetalt = ytelseskomponentersum;
        return this;
    }

    public Hovedytelse withSammenlagtTrekkBeloep() {
        this.sammenlagtTrekkBeloep = aggregateTrekkBeloep();
        return this;
    }

    public Hovedytelse withErHovedutbetaling(boolean erHovedutbetaling) {
        this.erHovedUtbetaling = erHovedutbetaling;
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        return id.equals(((Hovedytelse) other).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
