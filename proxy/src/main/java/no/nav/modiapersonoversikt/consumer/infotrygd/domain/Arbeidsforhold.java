package no.nav.modiapersonoversikt.consumer.infotrygd.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class Arbeidsforhold implements Serializable {
    private String arbeidsgiverNavn;
    private String arbeidsgiverKontonr;
    private Kodeverkstype inntektsperiode;
    private Double inntektForPerioden;
    private LocalDate sykepengerFom;
    private LocalDate refusjonTom;
    private Kodeverkstype refusjonstype;

    public Arbeidsforhold() {
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverKontonr() {
        return arbeidsgiverKontonr;
    }

    public void setArbeidsgiverKontonr(String arbeidsgiverKontonr) {
        this.arbeidsgiverKontonr = arbeidsgiverKontonr;
    }

    public LocalDate getRefusjonTom() {
        return refusjonTom;
    }

    public void setRefusjonTom(LocalDate refusjonTom) {
        this.refusjonTom = refusjonTom;
    }

    public LocalDate getSykepengerFom() {
        return sykepengerFom;
    }

    public void setSykepengerFom(LocalDate sykepengerFom) {
        this.sykepengerFom = sykepengerFom;
    }

    public Kodeverkstype getRefusjonstype() {
        return refusjonstype;
    }

    public void setRefusjonstype(Kodeverkstype refusjonstype) {
        this.refusjonstype = refusjonstype;
    }

    public Kodeverkstype getInntektsperiode() {
        return inntektsperiode;
    }

    public void setInntektsperiode(Kodeverkstype inntektsperiode) {
        this.inntektsperiode = inntektsperiode;
    }

    public Double getInntektForPerioden() {
        return inntektForPerioden;
    }

    public void setInntektForPerioden(Double inntektForPerioden) {
        this.inntektForPerioden = inntektForPerioden;
    }
}
