package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.Optional;
import java.util.stream.Stream;

import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.*;

public class GsakSakerService {

    private static final Logger logger = LoggerFactory.getLogger(GsakSakerService.class);

    public static final String GSAK_FAGSYSTEM_ID = "FS22";

    @Inject
    private SakV1 gsakSakV1;

    public Optional<Stream<Sak>> hentSaker(String fnr) {
        try {
            WSFinnSakResponse response = gsakSakV1.finnSak(new WSFinnSakRequest().withBruker(new WSPerson().withIdent(fnr)));
            return Optional.ofNullable(response.getSakListe()
                    .stream()
                    .map(gsakSak -> new Sak()
                                        .withSaksId(gsakSak.getSakId())
                                        .withTemakode(gsakSak.getFagomraade().getValue())
                                        .withBaksystem(GSAK)
                                        .withFagsystem(gsakSak.getFagsystem().getValue())
                                        .withFagsystem(GSAK_FAGSYSTEM_ID))
                    );
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            logger.warn("Det skjedde en ventet exception ved henting av Sakstema fra Gsak");
            return Optional.empty();
        } catch (SOAPFaultException e) {
            logger.error("Det skjedde en uventet feil mot Gsak", e);
            throw new FeilendeBaksystemException(GSAK);
        }
    }
}
