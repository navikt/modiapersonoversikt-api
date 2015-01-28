package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.utbetaling.Aktoer;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

public class Hovedytelse {

    private DateTime posteringsDato;
    private Aktoer utbetaltTil;
    private Double utbetalingNettopBeloep;
    private String utbetalingsmelding;
    private DateTime utbetalingsDato;
    private DateTime forfallsDato;
    private Konto utbetaltTilKonto;
    private String utbetalingsmetode;
    private String utbetalingsstatus;

    private String ytelsesType;
    private Interval ytelsesperiode;
    private List<Underytelse> underytelseListe;
    private Double sumUnderytelser;
    private List<Trekk> trekkListe;
    private List<Double> skattListe;
    private Double sumSkatt;
    private Double nettoBeloep;
    private String bilagsnummer;
    private Aktoer rettighetshaver;
    private Aktoer refundertForOrg;

    private Hovedytelse() {
        this.underytelseListe = new ArrayList<>();
        this.trekkListe = new ArrayList<>();
        this.skattListe = new ArrayList<>();
    }

    public class HovedytelseBuilder {
        private Hovedytelse hovedytelse;

        public HovedytelseBuilder() {
            this.hovedytelse = new Hovedytelse();
        }

        public HovedytelseBuilder withPosteringsDato(DateTime posteringsDato) {
            this.hovedytelse.posteringsDato = posteringsDato;
            return this;
        }

        public HovedytelseBuilder withUtbetaltTil(Aktoer utbetaltTil) {
            this.hovedytelse.utbetaltTil = utbetaltTil;
            return this;
        }

        public HovedytelseBuilder withUtbetalingNettoBeloep(Double utbetalingNettoBeloep) {
            this.hovedytelse.utbetalingNettopBeloep = utbetalingNettoBeloep;
            return this;
        }

        public HovedytelseBuilder withUtbetalingsmelding(String utbetalingsmelding) {
            this.hovedytelse.utbetalingsmelding = utbetalingsmelding;
            return this;
        }

        public HovedytelseBuilder withUtbetalingsDato(DateTime utbetalingsDato) {
            this.hovedytelse.utbetalingsDato = utbetalingsDato;
            return this;
        }

        public HovedytelseBuilder withForfallsDato(DateTime forfallsDato) {
            this.hovedytelse.forfallsDato = forfallsDato;
            return this;
        }

        public HovedytelseBuilder withUtbetaltTilKonto(Konto utbetaltTilKonto) {
            this.hovedytelse.utbetaltTilKonto = utbetaltTilKonto;
            return this;
        }

        public HovedytelseBuilder withUtbetalingsstatus(String utbetalingsstatus) {
            this.hovedytelse.utbetalingsstatus = utbetalingsstatus;
            return this;
        }

        public HovedytelseBuilder withUtbetalingsmetode(String utbetalingsmetode) {
            this.hovedytelse.utbetalingsmetode = utbetalingsmetode;
            return this;
        }

        public HovedytelseBuilder withYtelsestype(String ytelsestype) {
            this.hovedytelse.ytelsesType = ytelsestype;
            return this;
        }

        public HovedytelseBuilder withYtelsePeriode(Interval ytelsePeriode) {
            this.hovedytelse.ytelsesperiode = ytelsePeriode;
            return this;
        }

        public HovedytelseBuilder withUnderytelseListe(List<Underytelse> underytelseListe) {
            this.hovedytelse.underytelseListe = underytelseListe;
            return this;
        }

        public HovedytelseBuilder withSumUnderytelser(Double sumUnderytelser) {
            this.hovedytelse.sumUnderytelser= sumUnderytelser;
            return this;
        }

        public HovedytelseBuilder withTrekkliste(List<Trekk> trekkliste) {
            this.hovedytelse.trekkListe = trekkliste;
            return this;
        }

        public HovedytelseBuilder withSkatteListe(List<Double> skatteListe) {
            this.hovedytelse.skattListe = skatteListe;
            return this;
        }

        public HovedytelseBuilder withSumSkatt(Double sumSkatt) {
            this.hovedytelse.sumSkatt = sumSkatt;
            return this;
        }

        public HovedytelseBuilder withNettoBeloep(Double nettoBeloep) {
            this.hovedytelse.nettoBeloep = nettoBeloep;
            return this;
        }

        public HovedytelseBuilder withBilagsnummer(String bilagsnummer) {
            this.hovedytelse.bilagsnummer = bilagsnummer;
            return this;
        }

        public HovedytelseBuilder withRettighetshaver(Aktoer rettighetshaver) {
            this.hovedytelse.rettighetshaver = rettighetshaver;
            return this;
        }

        public HovedytelseBuilder withRefundertForOrg(Aktoer organisasjon) {
            this.hovedytelse.refundertForOrg = organisasjon;
            return this;
        }

        public Hovedytelse build() {
            return this.hovedytelse;
        }
    }
}
