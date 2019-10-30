package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSArbeidsfordelingKriterier;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSGeografi;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Optional;

@Configuration
public class ArbeidsfordelingV1Mock {

    @Bean
    public static ArbeidsfordelingV1 arbeidsfordelingV1() {
        return new ArbeidsfordelingV1() {
            @Override
            public WSFinnBehandlendeEnhetListeResponse finnBehandlendeEnhetListe(WSFinnBehandlendeEnhetListeRequest request) throws FinnBehandlendeEnhetListeUgyldigInput {
                String enhetsnummer = Optional.of(request)
                        .map(WSFinnBehandlendeEnhetListeRequest::getArbeidsfordelingKriterier)
                        .map(WSArbeidsfordelingKriterier::getGeografiskTilknytning)
                        .map(WSGeografi::getValue)
                        .orElse("0000");

                return new WSFinnBehandlendeEnhetListeResponse()
                        .withBehandlendeEnhetListe(Collections.singletonList(new WSOrganisasjonsenhet()
                                .withEnhetId(enhetsnummer)
                                .withEnhetNavn("Mock enhet")));
            }

            @Override
            public void ping() {

            }
        };
    }
}
