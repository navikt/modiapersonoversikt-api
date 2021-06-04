package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon;

import java.io.Serializable;
import java.util.Map;

public class Tekstobjekt extends IdentifiserbarEntitet implements Serializable {

    private Map<String,Tekst> tekst;

	public String getTekstForSpraak(String spraak) {
		return tekst.get(spraak).getTeksten();
	}
}
