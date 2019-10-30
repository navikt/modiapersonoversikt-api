package no.nav.personsok.domain.enums;

public enum PersonstatusType {
	DOED, BOSATT, UTVANDRET;

	@Override
	public String toString() {
		if (DOED == this) {
			return "^[D].*[D]$";
		} else {
			return this.name();
		}
	}
}
