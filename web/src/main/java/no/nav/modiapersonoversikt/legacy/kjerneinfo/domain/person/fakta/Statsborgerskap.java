package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;

import java.io.Serializable;

public class Statsborgerskap extends Kodeverdi implements Serializable {
	public String getLand() {
		return getBeskrivelse();
	}

	public void setLand(String land) {
		setBeskrivelse(land);
		setKodeRef(land);
	}
	@Override
	public String toString() {
		return getBeskrivelse();
	}
}