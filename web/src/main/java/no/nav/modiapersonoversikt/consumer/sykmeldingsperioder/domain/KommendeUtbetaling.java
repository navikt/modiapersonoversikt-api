package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain;


import org.joda.time.LocalDate;

public class KommendeUtbetaling extends Utbetaling {
    private LocalDate utbetalingsdato;
    private double bruttobeloep;
    private String arbeidsgiver;
    private String arbeidsgiverKontonr;
    private String arbeidsgiverOrgnr;
    private double dagsats;
    private String saksbehandler;
    private Kodeverkstype type;

    public LocalDate getUtbetalingsdato() {
        return utbetalingsdato;
    }

    public void setUtbetalingsdato(LocalDate utbetalingsdato) {
        this.utbetalingsdato = utbetalingsdato;
    }

    public double getBruttobeloep() {
        return bruttobeloep;
    }

    public void setBruttobeloep(double bruttobeloep) {
        this.bruttobeloep = bruttobeloep;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiver;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiver = arbeidsgiverNavn;
    }

    public String getArbeidsgiverKontonr() {
        return arbeidsgiverKontonr;
    }

    public void setArbeidsgiverKontonr(String arbeidsgiverKontonr) {
        this.arbeidsgiverKontonr = arbeidsgiverKontonr;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public void setArbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
    }

    public double getDagsats() {
        return dagsats;
    }

    public void setDagsats(double dagsats) {
        this.dagsats = dagsats;
    }

    public String getSaksbehandler() {
        return saksbehandler;
    }

    public void setSaksbehandler(String saksbehandler) {
        this.saksbehandler = saksbehandler;
    }

    public KommendeUtbetaling() {
    }

    public Kodeverkstype getType() {
        return type;
    }

    public void setType(Kodeverkstype type) {
        this.type = type;
    }
}