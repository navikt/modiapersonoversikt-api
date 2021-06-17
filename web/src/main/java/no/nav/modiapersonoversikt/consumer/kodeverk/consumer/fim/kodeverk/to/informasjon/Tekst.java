package no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.informasjon;

import java.io.Serializable;

public class Tekst extends IdentifiserbarEntitet implements Serializable {

    private String spraak;
	private String teksten;

	public String getSpraak() {
		return spraak;
	}

	public void setSpraak(String spraak) {
		this.spraak = spraak;
	}

	public String getTeksten() {
		return teksten;
	}

	public void setTeksten(String teksten) {
		this.teksten = teksten;
	}

}
