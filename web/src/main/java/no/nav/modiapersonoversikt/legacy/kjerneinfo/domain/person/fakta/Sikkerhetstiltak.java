package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class Sikkerhetstiltak implements Serializable {
	private Periode periode;

	public Periode getPeriode() {
		return periode;
	}

	public void setPeriode(Periode periode) {
		this.periode = periode;
	}
}
