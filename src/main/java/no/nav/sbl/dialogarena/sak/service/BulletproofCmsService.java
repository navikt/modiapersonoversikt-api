package no.nav.sbl.dialogarena.sak.service;

public interface BulletproofCmsService {
    String hentTekst(String key);

    Boolean eksistererTekst(String key);

    Boolean eksistererArtikkel(String key);

    String hentArtikkel(String key);
}
