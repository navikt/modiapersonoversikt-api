package no.nav.modiapersonoversikt.consumer.personsok.domain;

public enum Kjonn {
	M, K, BLANK;

	@Override
	public String toString() {
		String kjonn = super.toString();
		if (BLANK.name().equals(kjonn)) {
			return "";
		} else {
			return kjonn;
		}

	}
}
