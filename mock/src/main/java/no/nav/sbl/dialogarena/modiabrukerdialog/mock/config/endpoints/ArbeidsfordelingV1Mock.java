package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.FinnAlleBehandlendeEnheterListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.binding.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.ArbeidsfordelingKriterier;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.Geografi;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.Organisasjonsenhet;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnAlleBehandlendeEnheterListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnAlleBehandlendeEnheterListeResponse;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.FinnBehandlendeEnhetListeResponse;
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
            public FinnBehandlendeEnhetListeResponse finnBehandlendeEnhetListe(FinnBehandlendeEnhetListeRequest request) throws FinnBehandlendeEnhetListeUgyldigInput {
                String enhetsnummer = Optional.of(request)
                        .map(FinnBehandlendeEnhetListeRequest::getArbeidsfordelingKriterier)
                        .map(ArbeidsfordelingKriterier::getGeografiskTilknytning)
                        .map(Geografi::getValue)
                        .orElse("0000");

                Organisasjonsenhet enhet = new Organisasjonsenhet();
                enhet.setEnhetId(enhetsnummer);
                enhet.setEnhetNavn("Mock enhet");
                FinnBehandlendeEnhetListeResponse response = new FinnBehandlendeEnhetListeResponse();
                response.getBehandlendeEnhetListe().add(enhet);

                return response;
            }

            @Override
            public void ping() {

            }

            @Override
            public FinnAlleBehandlendeEnheterListeResponse finnAlleBehandlendeEnheterListe(FinnAlleBehandlendeEnheterListeRequest request) throws FinnAlleBehandlendeEnheterListeUgyldigInput {
                String enhetsnummer = Optional.of(request)
                        .map(FinnAlleBehandlendeEnheterListeRequest::getArbeidsfordelingKriterier)
                        .map(ArbeidsfordelingKriterier::getGeografiskTilknytning)
                        .map(Geografi::getValue)
                        .orElse("0000");

                Organisasjonsenhet enhet = new Organisasjonsenhet();
                enhet.setEnhetId(enhetsnummer);
                enhet.setEnhetNavn("Mock enhet");
                FinnAlleBehandlendeEnheterListeResponse response = new FinnAlleBehandlendeEnheterListeResponse();
                response.getBehandlendeEnhetListe().add(enhet);

                return response;
            }
        };
    }
}
