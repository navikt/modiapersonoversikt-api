package no.nav.personsok.consumer.fim.personsok.mock;

import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Landkoder;

public class UstrukturertadresseMock {

	private Landkoder landkode;
	private String adresselinje1;
	private String adresselinje2;
	private String adresselinje3;

	public UstrukturertadresseMock() {
	}

	public UstrukturertadresseMock(Landkoder landkode, String adresselinje1, String adresselinje2, String adresselinje3) {
		this.landkode = landkode;
		this.adresselinje1 = adresselinje1;
		this.adresselinje2 = adresselinje2;
		this.adresselinje3 = adresselinje3;
	}

	public String getAdresseLinje1() {
		return adresselinje1;
	}

	public void setAdresseLinje1(String adresselinje1) {
		this.adresselinje1 = adresselinje1;
	}

	public String getAdresseLinje2() {
		return adresselinje2;
	}

	public void setAdresseLinje2(String adresselinje2) {
		this.adresselinje2 = adresselinje2;
	}

	public String getAdresseLinje3() {
		return adresselinje3;
	}

	public void setAdresseLinje3(String adresselinje3) {
		this.adresselinje3 = adresselinje3;
	}

	public Landkoder getLandkode() {
		return landkode;
	}

	public void setLandkode(Landkoder landkode) {
		this.landkode = landkode;
	}
}
