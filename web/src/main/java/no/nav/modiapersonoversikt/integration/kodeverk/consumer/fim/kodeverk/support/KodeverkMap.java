package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.support;

import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon.EnkeltKodeverk;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon.Kode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class KodeverkMap implements Serializable {
	private static Map<String, EnkeltKodeverk> map = new HashMap<String, EnkeltKodeverk>();

	
	public int size() {
		return map.size();
	}

	public EnkeltKodeverk get(Object key) {
		return map.get(key);
	}

	
	public EnkeltKodeverk put(String key, EnkeltKodeverk value) {
		return map.put(key, value);
	}

	
	public EnkeltKodeverk remove(Object key) {
		return map.remove(key);
	}

	public String getBeskrivelse(String koderef, String kodeverksref, String spraak) {
		String beskrivelse = null;
		if (map.get(kodeverksref) != null) {
			EnkeltKodeverk enkeltKodeverk = KodeverkMap.map.get(kodeverksref);
			if (enkeltKodeverk.get(koderef) != null) {
				Kode kode = enkeltKodeverk.get(koderef);
				if (kode.getTermForSpraak(spraak) != null) {
					beskrivelse = kode.getTermForSpraak(spraak).getNavn();
				}
			}
		}
		return beskrivelse;
	}
}