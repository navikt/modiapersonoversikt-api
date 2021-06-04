package no.nav.modiapersonoversikt.integration.personsok.domain;

import org.joda.time.LocalDate;

import java.io.Serializable;

public class UtvidetPersonsok implements Serializable {

	private String fornavn;
	private String etternavn;
	private String gatenavn;
	private String husnummer;
	private String husbokstav;
	private String postnummer;
	private String kontonummer;
	private String kommunenr;
	private LocalDate fodselsdatoFra;
	private LocalDate fodselsdatoTil;
	private Integer alderFra;
	private Integer alderTil;
	private Kjonn kjonn;

	public UtvidetPersonsok() {
		kjonn = Kjonn.BLANK;
	}

	public String getGatenavn() {
		return gatenavn;
	}

	public void setGatenavn(String gatenavn) {
		this.gatenavn = gatenavn;
	}

	public String getHusnummer() {
		return husnummer;
	}

	public void setHusnummer(String husnummer) {
		this.husnummer = husnummer;
	}

	public String getHusbokstav() {
		return husbokstav;
	}

	public void setHusbokstav(String husbokstav) {
		this.husbokstav = husbokstav;
	}

	public String getPostnummer() {
		return postnummer;
	}

	public void setPostnummer(String postnummer) {
		this.postnummer = postnummer;
	}

	public String getKontonummer() {
		return kontonummer;
	}

	public void setKontonummer(String kontonummer) {
		this.kontonummer = kontonummer;
	}

	public String getKommunenr() {
		return kommunenr;
	}

	public void setKommunenr(String kommunenr) {
		this.kommunenr = kommunenr;
	}

	public Integer getAlderFra() {
		return alderFra;
	}

	public void setAlderFra(Integer alderFra) {
		this.alderFra = alderFra;
	}

	public Integer getAlderTil() {
		return alderTil;
	}

	public void setAlderTil(Integer alderTil) {
		this.alderTil = alderTil;
	}

	public Kjonn getKjonn() {
		return kjonn;
	}

	public void setKjonn(Kjonn kjonn) {
		this.kjonn = kjonn;
	}

	public String getFornavn() {
		return fornavn;
	}

	public void setFornavn(String fornavn) {
		this.fornavn = fornavn;
	}

	public String getEtternavn() {
		return etternavn;
	}

	public void setEtternavn(String etternavn) {
		this.etternavn = etternavn;
	}

	public LocalDate getFodselsdatoFra() {
		return fodselsdatoFra;
	}

	public void setFodselsdatoFra(LocalDate fodselsdatoFra) {
		this.fodselsdatoFra = fodselsdatoFra;
	}

	public LocalDate getFodselsdatoTil() {
		return fodselsdatoTil;
	}

	public void setFodselsdatoTil(LocalDate fodselsdatoTil) {
		this.fodselsdatoTil = fodselsdatoTil;
	}
}