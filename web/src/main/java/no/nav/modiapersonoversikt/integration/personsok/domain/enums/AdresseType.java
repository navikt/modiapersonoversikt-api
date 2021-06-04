package no.nav.modiapersonoversikt.integration.personsok.domain.enums;

public enum AdresseType {

	BOLIGADRESSE("B"), POSTADRESSE("P"), MIDLERTIDIG_POSTADRESSE_NORGE("M"), MIDLERTIDIG_POSTADRESSE_UTLAND("U"), INGEN("");

	private String value;

	AdresseType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public String toString() {
		return value;
	}
}
