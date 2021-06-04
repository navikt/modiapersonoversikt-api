package no.nav.personsok.domain;

import no.nav.personsok.domain.enums.AdresseType;

import java.io.Serializable;

public class Adresse implements Serializable {

	private String adresseString;

	private AdresseType adresseType;

	public Adresse() {
	}

	public Adresse(String adresseString, AdresseType adresseType) {
		this.adresseString = adresseString;
		this.adresseType = adresseType;
	}

	public String getAdresseString() {
		return adresseString;
	}

	public void setAdresseString(String adresseString) {
		this.adresseString = adresseString;
	}

	public String getAdresseStringMedType() {
		return adresseString + "(" + adresseType.toString() + ")";
	}

	public AdresseType getAdresseType() {
		return adresseType;
	}

	public void setAdresseType(AdresseType adresseType) {
		this.adresseType = adresseType;
	}
}
