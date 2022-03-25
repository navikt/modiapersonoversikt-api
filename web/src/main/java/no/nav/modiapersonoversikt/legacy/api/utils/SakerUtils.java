package no.nav.modiapersonoversikt.legacy.api.utils;

import no.nav.modiapersonoversikt.service.saker.Sak;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;

import java.util.List;


public class SakerUtils {

    public static void leggTilFagsystemnavnOgTemanavn(
            List<Sak> sakerForBruker,
            final EnhetligKodeverk.Kodeverk<String, String> fagsystemKodeverk,
            final EnhetligKodeverk.Kodeverk<String, String> arkivtemaKodeverk
    ) {
        sakerForBruker.forEach(sak -> {
            sak.fagsystemNavn = fagsystemKodeverk.hentVerdi(sak.fagsystemKode, sak.fagsystemKode);
            sak.temaNavn = arkivtemaKodeverk.hentVerdi(sak.temaKode, sak.temaKode);
        });
    }

}
