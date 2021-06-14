package no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.informasjon;

import java.io.Serializable;
import java.util.Map;

public class EnkeltKodeverk extends Kodeverk implements Serializable {
    private Map<String, Kode> kode;

    public Kode get(String key) {
        return kode.get(key);
    }

    public void put(String key, Kode kode) {
        this.kode.put(key, kode);
    }

    public Map<String, Kode> getKode() {
        return kode;
    }

    public void setKode(Map<String, Kode> kode) {
        this.kode = kode;
    }
}
