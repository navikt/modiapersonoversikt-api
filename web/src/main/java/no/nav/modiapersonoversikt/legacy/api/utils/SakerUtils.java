package no.nav.modiapersonoversikt.legacy.api.utils;

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig;

import java.util.List;
import java.util.Map;


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
