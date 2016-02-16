package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class PesysService {
    private static final Logger LOGGER = getLogger(PesysService.class);

    public static String PESYS_FAGSYSTEM_ID = "PEN";

    @Inject
    private PensjonSakV1 pensjonSakV1;

    @Cacheable("pesysCache")
    public List<Sak> hentSakstemaFraPesys(String uId, String sessionId) {
        try {
            WSHentSakSammendragListeResponse wsHentSakSammendragListeResponse = pensjonSakV1.hentSakSammendragListe(
                    new WSHentSakSammendragListeRequest()
                            .withPersonident(uId));
            return wsHentSakSammendragListeResponse.getSakSammendragListe()
                    .stream()
                    .map(sakssammendrag -> new Sak()
                                .withSaksId(sakssammendrag.getSakId())
                                .withTemakode(sakssammendrag.getArkivtema().getValue())
                                .withBaksystem(Baksystem.PESYS)
                                .withFagsystem(PESYS_FAGSYSTEM_ID))
                    .collect(Collectors.toList());
        } catch (HentSakSammendragListeSakManglerEierenhet | HentSakSammendragListePersonIkkeFunnet e) {
            LOGGER.info("Det skjedde en ventet exception ved henting av Sakstema fra Pesys");
            return new ArrayList<>();
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Pesys", e);
            return new ArrayList<>();
        }
    }
}
