package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.sak.viewdomain.detalj.Sak;
import no.nav.sbl.dialogarena.sak.viewdomain.detalj.SakGsak;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class GsakSakerService {

    @Inject
    private SakV1 gsakSakV1;

    public Optional<Stream<Sak>> hentSaker(String fnr) {
        try {
            WSFinnSakResponse response = gsakSakV1.finnSak(new WSFinnSakRequest().withBruker(new WSPerson().withIdent(fnr)));
            return Optional.ofNullable(response.getSakListe()
                    .stream()
                    .map(gsakSak ->
                        new Sak()
                                .withSaksId(gsakSak.getSakId())
                                .withTemakode(gsakSak.getFagomraade().getValue())
                                .withBaksystem(Baksystem.GSAK)
                                .withFagsystem(gsakSak.getFagsystem().getValue())
                    ));
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            return Optional.empty();
        }
    }

    public Optional<Stream<SakGsak>> hentGsakSaker(String fnr) {
        try {
            WSFinnSakResponse response = gsakSakV1.finnSak(new WSFinnSakRequest()
                    .withBruker(new WSPerson().withIdent(fnr)));
            return Optional.ofNullable(response.getSakListe()
                    .stream()
                    .map(wsSak -> {
                        SakGsak sak = new SakGsak();
                        sak.opprettetDato = wsSak.getOpprettelsetidspunkt();
                        sak.saksId = Optional.ofNullable(wsSak.getSakId());
                        sak.fagsystemSaksId = isBlank(wsSak.getFagsystemSakId()) ? Optional.empty() : Optional.ofNullable(wsSak.getFagsystemSakId());
                        sak.temaKode = wsSak.getFagomraade().getValue();
                        sak.sakstype = wsSak.getSakstype().getValue();
                        sak.fagsystemKode = wsSak.getFagsystem().getValue();
                        sak.finnesIGsak = true;
                        return sak;
                    })
            );
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            return Optional.empty();
        }
    }
}
