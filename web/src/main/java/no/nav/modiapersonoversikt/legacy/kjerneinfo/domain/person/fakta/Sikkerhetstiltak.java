package no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class Sikkerhetstiltak implements Serializable {
	private Periode periode;
	private String sikkerhetstiltaksbeskrivelse;
	private String sikkerhetstiltakskode;

	public Periode getPeriode() {
		return periode;
	}

	public void setPeriode(Periode periode) {
		this.periode = periode;
	}

	public String getSikkerhetstiltakskode() {
		return sikkerhetstiltakskode;
	}

	public void setSikkerhetstiltakskode(String sikkerhetstiltakskode) {
		this.sikkerhetstiltakskode = sikkerhetstiltakskode;
	}

	public String getSikkerhetstiltaksbeskrivelse() {
		return sikkerhetstiltaksbeskrivelse;
	}

	public void setSikkerhetstiltaksbeskrivelse(String sikkerhetstiltaksbeskrivelse) {
		this.sikkerhetstiltaksbeskrivelse = sikkerhetstiltaksbeskrivelse;
	}
}
