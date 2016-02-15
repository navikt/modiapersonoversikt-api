package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Sak;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.PensjonSakV1;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeRequest;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.meldinger.WSHentSakSammendragListeResponse;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class PesysServiceImpl implements PesysService {
    private static final Logger LOGGER = getLogger(PesysServiceImpl.class);

    @Inject
    private PensjonSakV1 pensjonSakV1;

    @Override
    public List<Sak> hentSakstemaFraPesys(String uId) {
        try {
            WSHentSakSammendragListeResponse wsHentSakSammendragListeResponse = pensjonSakV1.hentSakSammendragListe(
                    new WSHentSakSammendragListeRequest()
                            .withPersonident(uId));
            return wsHentSakSammendragListeResponse.getSakSammendragListe()
                    .stream()
                    .map(sakssammendrag ->
                            new Sak()
                                .withSaksId(sakssammendrag.getSakId())
                                .withTemakode(sakssammendrag.getArkivtema().getValue())
                                .withBaksystem(Baksystem.PESYS)
                                .withFagsystem("PEN"))
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
