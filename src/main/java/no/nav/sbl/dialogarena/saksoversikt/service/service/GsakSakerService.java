package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

public class GsakSakerService {

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
                                        .withBaksystem(Baksystem.GSAK)
                                        .withFagsystem(gsakSak.getFagsystem().getValue())
                                        .withFagsystem(GSAK_FAGSYSTEM_ID))
                    );
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            return Optional.empty();
        }
    }
}
