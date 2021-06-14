package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.foreldrepenger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.Bruker;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.CommonFakta;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.Kodeverkstype;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Foreldrepengerettighet extends CommonFakta implements Serializable {

    private String andreForeldersFnr;
    private Integer antallBarn;
    private LocalDate barnetsFoedselsdato;
    private Bruker forelder;
    private Double dekningsgrad;
    private LocalDate fedrekvoteTom;
    private LocalDate moedrekvoteTom;
    private Kodeverkstype foreldrepengetype;
    private Integer graderingsdager;
    private Integer restDager;
    private LocalDate rettighetFom;
	private LocalDate eldsteIdDato;
    private List<Foreldrepengeperiode> periode;
    private Kodeverkstype foreldreAvSammeKjoenn;

    public Foreldrepengerettighet() {
    }

    public String getAndreForeldersFnr() {
        return andreForeldersFnr;
    }

    public void setAndreForeldersFnr(String value) {
        this.andreForeldersFnr = value;
    }

    public LocalDate getBarnetsFoedselsdato() {
        return barnetsFoedselsdato;
    }

    public void setBarnetsFoedselsdato(LocalDate value) {
        this.barnetsFoedselsdato = value;
    }

    public Double getDekningsgrad() {
        return dekningsgrad;
    }

    public void setDekningsgrad(Double value) {
        this.dekningsgrad = value;
    }

    public LocalDate getFedrekvoteTom() {
        return fedrekvoteTom;
    }

    public void setFedrekvoteTom(LocalDate value) {
        this.fedrekvoteTom = value;
    }

    public Kodeverkstype getForeldrepengetype() {
        return foreldrepengetype;
    }

    public void setForeldrepengetype(Kodeverkstype value) {
        this.foreldrepengetype = value;
    }

    public Integer getGraderingsdager() {
        return graderingsdager;
    }

    public void setGraderingsdager(Integer value) {
        this.graderingsdager = value;
    }

    public Integer getRestDager() {
        return restDager;
    }

    public void setRestDager(Integer value) {
        this.restDager = value;
    }

    public LocalDate getRettighetFom() {
        if (rettighetFom != null) {
            return rettighetFom;
        } else {
            return barnetsFoedselsdato;
        }
    }

    public void setRettighetFom(LocalDate value) {
        this.rettighetFom = value;
    }

    public Bruker getForelder() {
        return forelder;
    }

    public void setForelder(Bruker value) {
        this.forelder = value;
    }

    public List<Foreldrepengeperiode> getPeriode() {
        if (periode == null) {
            periode = new ArrayList<>();
        }
        return periode;
    }

    public void setPeriode(List<Foreldrepengeperiode> periode) {
        this.periode = periode;
    }

    public Integer getAntallBarn() {
        return this.antallBarn;
    }

    public void setAntallBarn(Integer antallBarn) {
        this.antallBarn = antallBarn;
    }

    public LocalDate getMoedrekvoteTom() {
        return this.moedrekvoteTom;
    }

    public void setMoedrekvoteTom(LocalDate moedrekvoteTom) {
        this.moedrekvoteTom = moedrekvoteTom;
    }

    public Kodeverkstype getForeldreAvSammeKjoenn() {
        return foreldreAvSammeKjoenn;
    }

    public void setForeldreAvSammeKjoenn(Kodeverkstype foreldreAvSammeKjoenn) {
        this.foreldreAvSammeKjoenn = foreldreAvSammeKjoenn;
    }

	public LocalDate getEldsteIdDato() {
		return eldsteIdDato;
	}

	public void setEldsteIdDato(LocalDate eldsteIdDato) {
		this.eldsteIdDato = eldsteIdDato;
	}
}
