package no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.pleiepenger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Arbeidsforhold implements Serializable {
    private String arbeidsgiverNavn;
    private String arbeidsgiverKontonr;
    private String inntektsperiode;
    private BigDecimal inntektForPerioden;
    private LocalDate refusjonTom;
    private String refusjonstype;
    private String arbeidsgiverOrgnr;
    private String arbeidskategori;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverKontonr() {
        return arbeidsgiverKontonr;
    }

    public LocalDate getRefusjonTom() {
        return refusjonTom;
    }

    public String getRefusjonstype() {
        return refusjonstype;
    }

    public String getInntektsperiode() {
        return inntektsperiode;
    }

    public BigDecimal getInntektForPerioden() {
        return inntektForPerioden;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public String getArbeidskategori() {
        return arbeidskategori;
    }

    public Arbeidsforhold withArbeidsgiverOrgnr(String arbeidsgiverOrgnr) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
        return this;
    }

    public Arbeidsforhold withArbeidsgiverKontonr(String arbeidsgiverKontonr) {
        this.arbeidsgiverKontonr = arbeidsgiverKontonr;
        return this;
    }

    public Arbeidsforhold withInntektForPerioden(BigDecimal inntektForPerioden) {
        this.inntektForPerioden = inntektForPerioden;
        return this;
    }

    public Arbeidsforhold withInntektsperiode(String inntektsperiode) {
        this.inntektsperiode = inntektsperiode;
        return this;
    }

    public Arbeidsforhold withRefusjonstype(String refusjonstype) {
        this.refusjonstype = refusjonstype;
        return this;
    }

    public Arbeidsforhold withRefusjonTom(LocalDate refusjonTom) {
        this.refusjonTom = refusjonTom;
        return this;
    }

    public Arbeidsforhold withArbeidskategori(String arbeidskategori) {
        this.arbeidskategori = arbeidskategori;
        return this;
    }

}
