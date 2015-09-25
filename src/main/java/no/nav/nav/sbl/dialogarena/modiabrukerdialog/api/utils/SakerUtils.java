package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import org.apache.commons.collections15.Closure;

import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;

public class SakerUtils {

    public static void leggTilFagsystemnavnOgTemanavn(List<Sak> sakerForBruker, final Map<String, String> fagsystemMapping, final StandardKodeverk standardKodeverk) {
        on(sakerForBruker).forEach(new Closure<Sak>() {
            @Override
            public void execute(Sak sak) {
                String fagsystemnavn = fagsystemMapping.get(sak.fagsystemKode);
                sak.fagsystemNavn = fagsystemnavn != null ? fagsystemnavn : sak.fagsystemKode;

                String temaNavn = standardKodeverk.getArkivtemaNavn(sak.temaKode);
                sak.temaNavn = temaNavn != null ? temaNavn : sak.temaKode;
            }
        });
    }

}
