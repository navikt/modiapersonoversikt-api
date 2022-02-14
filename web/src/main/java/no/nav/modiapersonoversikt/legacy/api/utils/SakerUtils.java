package no.nav.modiapersonoversikt.legacy.api.utils;

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig;

import java.util.List;
import java.util.Map;


public class SakerUtils {

    public static void leggTilFagsystemnavnOgTemanavn(List<Sak> sakerForBruker, final Map<String, String> fagsystemMapping, final EnhetligKodeverk.Service kodeverk) {
        sakerForBruker.forEach(sak -> {
            String fagsystemnavn = fagsystemMapping.get(sak.fagsystemKode);
            sak.fagsystemNavn = fagsystemnavn != null ? fagsystemnavn : sak.fagsystemKode;

            String temaNavn = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).hentVerdi(sak.temaKode, sak.temaKode);
            sak.temaNavn = temaNavn != null ? temaNavn : sak.temaKode;
        });
    }

}
