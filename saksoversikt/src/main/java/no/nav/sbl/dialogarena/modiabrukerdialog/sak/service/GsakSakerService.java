package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.GSAK;

public class GsakSakerService {

    private static final Logger logger = LoggerFactory.getLogger(GsakSakerService.class);

    public static final String GSAK_FAGSYSTEM_ID = "FS22";

    @Inject
    private SakV1 gsakSakV1;

    public Optional<Stream<Sak>> hentSaker(String fnr) {
        try {
            WSFinnSakResponse response = gsakSakV1.finnSak(new WSFinnSakRequest().withBruker(new WSPerson().withIdent(fnr)));
            return Optional.of(response.getSakListe()
                    .stream()
                    .map(gsakSak -> new Sak()
                                        .withSaksId(gsakSak.getSakId())
                                        .withFagsaksnummer(gsakSak.getFagsystemSakId())
                                        .withTemakode(gsakSak.getFagomraade().getValue())
                                        .withBaksystem(GSAK)
                                        .withFagsystem(gsakSak.getFagsystem().getValue())
                                        .withFagsystem(GSAK_FAGSYSTEM_ID))
                    );
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            logger.error("Det skjedde en ventet exception ved henting av Sakstema fra Gsak", e);
            return Optional.empty();
        } catch (RuntimeException e) {
            logger.error("Det skjedde en uventet feil mot Gsak", e);
            throw new FeilendeBaksystemException(GSAK);
        }
    }
}
