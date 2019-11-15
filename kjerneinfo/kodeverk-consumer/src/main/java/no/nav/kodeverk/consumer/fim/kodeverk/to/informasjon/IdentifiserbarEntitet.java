package no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon;

import java.io.Serializable;

public class IdentifiserbarEntitet implements Serializable {

    private String navn;
    private String uri;


	public String getNavn() {
		return navn;
	}

	public void setNavn(String navn) {
		this.navn = navn;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
