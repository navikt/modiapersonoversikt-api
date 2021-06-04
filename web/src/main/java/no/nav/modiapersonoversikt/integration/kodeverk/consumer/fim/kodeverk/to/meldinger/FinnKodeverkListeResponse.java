package no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.meldinger;

import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk;

import java.io.Serializable;
import java.util.Map;

public class FinnKodeverkListeResponse implements Serializable {
    private Map<String, Kodeverk> kodeverkListe;

    public String getVersjonAvKodeverk(String kodeverk) {
        if (kodeverkListe != null) {
            Kodeverk result = kodeverkListe.get(kodeverk);
            if (result != null) {
                return result.getVersjonsnummer();
            }
        }
        return null;
    }

    public Map<String, Kodeverk> getKodeverkListe() {
        return kodeverkListe;
    }

    public void setKodeverkListe(Map<String, Kodeverk> kodeverkListe) {
        this.kodeverkListe = kodeverkListe;
    }
}
