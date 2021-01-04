package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;

import java.util.List;
import java.util.Map;


public class SakerUtils {

    public static void leggTilFagsystemnavnOgTemanavn(List<Sak> sakerForBruker, final Map<String, String> fagsystemMapping, final StandardKodeverk standardKodeverk) {
        sakerForBruker.forEach(sak -> {
            String fagsystemnavn = fagsystemMapping.get(sak.fagsystemKode);
            sak.fagsystemNavn = fagsystemnavn != null ? fagsystemnavn : sak.fagsystemKode;

            String temaNavn = standardKodeverk.getArkivtemaNavn(sak.temaKode);
            sak.temaNavn = temaNavn != null ? temaNavn : sak.temaKode;
        });
    }

}
