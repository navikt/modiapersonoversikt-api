package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.PESYS;
import static org.slf4j.LoggerFactory.getLogger;

public class PesysService {
    private static final Logger LOGGER = getLogger(PesysService.class);

    public static final String PESYS_FAGSYSTEM_ID = "PEN";

    @Inject
    private PensjonSakV1 pensjonSakV1;

    public Optional<Stream<Sak>> hentSakstemaFraPesys(String uId) {
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
                                .withFagsystem(PESYS_FAGSYSTEM_ID)))
                    ;
        } catch (HentSakSammendragListeSakManglerEierenhet | HentSakSammendragListePersonIkkeFunnet e) {
            LOGGER.error("Det skjedde en ventet exception ved henting av Sakstema fra Pesys", e);
            return Optional.empty();
        } catch (RuntimeException e) {
            LOGGER.error("Det skjedde en uventet feil mot Pesys", e);
            throw new FeilendeBaksystemException(PESYS);
        }
    }
}
