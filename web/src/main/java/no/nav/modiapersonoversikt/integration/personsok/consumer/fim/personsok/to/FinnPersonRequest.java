package no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.to;

import no.nav.modiapersonoversikt.integration.personsok.domain.UtvidetPersonsok;

import java.io.Serializable;

public class FinnPersonRequest implements Serializable{

	private UtvidetPersonsok utvidetPersonsok;

	public UtvidetPersonsok getUtvidetPersonsok() {
		return utvidetPersonsok;
	}

	public void setUtvidetPersonsok(UtvidetPersonsok utvidetPersonsok) {
		this.utvidetPersonsok = utvidetPersonsok;
	}
}
