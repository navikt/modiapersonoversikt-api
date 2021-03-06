package no.nav.modiapersonoversikt.consumer.kodeverk2;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modiapersonoversikt.consumer.kodeverk2.Kodeverk.Nokkel.TITTEL;

/**
 * Baseklasse for implementasjon av kodeverk-interface
 */
abstract class BaseKodeverk implements Kodeverk {

    protected Map<String, KodeverkElement> dbSkjema = new HashMap<>();

    protected Map<String, KodeverkElement> dbVedlegg = new HashMap<>();

    @Override
    public boolean isEgendefinert(String vedleggsIdOrskjemaId) {
        return ANNET.equals(vedleggsIdOrskjemaId);
    }

    @Override
    public String getTittel(String vedleggsIdOrskjemaId) {
        return getKode(vedleggsIdOrskjemaId, TITTEL);
    }

    @Override
    public String getKode(String vedleggsIdOrskjemaId, Nokkel nokkel) {
        return getKoder(vedleggsIdOrskjemaId).get(nokkel);
    }

    @Override
    public Map<Nokkel, String> getKoder(String vedleggsIdOrSkjemaId) {
        if (dbSkjema.containsKey(vedleggsIdOrSkjemaId)) {
            return dbSkjema.get(vedleggsIdOrSkjemaId).getKoderMap();
        } else {
            if (dbVedlegg.containsKey(vedleggsIdOrSkjemaId)) {
                return dbVedlegg.get(vedleggsIdOrSkjemaId).getKoderMap();
            }
        }
        throw new ApplicationException("\n ---- Fant ikke kodeverk : " + vedleggsIdOrSkjemaId + "---- \n");
    }

    public void setDbSkjema(Map<String, KodeverkElement> dbSkjema) {
        this.dbSkjema = dbSkjema;
    }

    public void setDbVedlegg(Map<String, KodeverkElement> dbVedlegg) {
        this.dbVedlegg = dbVedlegg;
    }
}