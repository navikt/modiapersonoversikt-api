package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.meldinger;

import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon.EnkeltKodeverk;

import java.io.Serializable;

public class HentKodeverkResponse implements Serializable {
	private EnkeltKodeverk kodeverk;

	public EnkeltKodeverk getKodeverk() {
		return kodeverk;
	}

	public void setKodeverk(EnkeltKodeverk kodeverk) {
		this.kodeverk = kodeverk;
	}
}
