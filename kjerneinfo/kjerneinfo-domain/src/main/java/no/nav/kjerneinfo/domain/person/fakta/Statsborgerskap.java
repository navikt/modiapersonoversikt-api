package no.nav.kjerneinfo.domain.person.fakta;

import no.nav.kjerneinfo.common.domain.Kodeverdi;

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