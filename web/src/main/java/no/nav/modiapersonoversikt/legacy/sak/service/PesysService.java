package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.FeilendeBaksystemException;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem.PESYS;
import static org.slf4j.LoggerFactory.getLogger;

public class PesysService {
    private static final Logger LOGGER = getLogger(PesysService.class);

    public static final String PESYS_FAGSYSTEM_ID = "PEN";

    @Autowired
    private PensjonSakV1 pensjonSakV1;

    public Optional<List<Sak>> hentSakstemaFraPesys(String uId) {
        try {
            WSHentSakSammendragListeResponse wsHentSakSammendragListeResponse = pensjonSakV1.hentSakSammendragListe(
                    new WSHentSakSammendragListeRequest()
                            .withPersonident(uId));
            return Optional.of(wsHentSakSammendragListeResponse.getSakSammendragListe()
                    .stream()
                    .map(sakssammendrag -> new Sak()
                                .withSaksId(sakssammendrag.getSakId())
                                .withTemakode(sakssammendrag.getArkivtema().getValue())
                                .withBaksystem(PESYS)
                                .withFagsystem(PESYS_FAGSYSTEM_ID))
                    .collect(toList())
            );
        } catch (HentSakSammendragListeSakManglerEierenhet | HentSakSammendragListePersonIkkeFunnet e) {
            LOGGER.error("Det skjedde en ventet exception ved henting av Sakstema fra Pesys", e);
            return Optional.empty();
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Pesys", e);
            throw new FeilendeBaksystemException(PESYS);
        }
    }
}
