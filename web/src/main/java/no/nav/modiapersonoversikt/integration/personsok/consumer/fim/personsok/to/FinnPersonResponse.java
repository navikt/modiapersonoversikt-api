package no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.to;

import no.nav.modiapersonoversikt.integration.personsok.domain.Person;

import java.io.Serializable;
import java.util.List;

public class FinnPersonResponse implements Serializable {

	protected int totaltAntallTreff;
	protected List<Person> personListe;

	public int getTotaltAntallTreff() {
		return totaltAntallTreff;
	}

	public void setTotaltAntallTreff(int totaltAntallTreff) {
		this.totaltAntallTreff = totaltAntallTreff;
	}

	public List<Person> getPersonListe() {
		return personListe;
	}

	public void setPersonListe(List<Person> personListe) {
		this.personListe = personListe;
	}
}
