package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.WSDetaljertEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.WSEnheterForGeografiskNedslagsfelt;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OrganisasjonEnhetV1Mock {

    @Bean
    public static OrganisasjonEnhetV1 organisasjonEnhetV1() {
        return new OrganisasjonEnhetV1() {
            @Override
            public WSFinnEnheterForArbeidsfordelingBolkResponse finnEnheterForArbeidsfordelingBolk(final WSFinnEnheterForArbeidsfordelingBolkRequest wsFinnEnheterForArbeidsfordelingBolkRequest) throws FinnEnheterForArbeidsfordelingBolkUgyldigInput {
                return null;
            }

            @Override
            public WSFinnArbeidsfordelingBolkResponse finnArbeidsfordelingBolk(final WSFinnArbeidsfordelingBolkRequest wsFinnArbeidsfordelingBolkRequest) throws FinnArbeidsfordelingBolkUgyldigInput {
                return null;
            }

            @Override
            public WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse finnNAVKontorForGeografiskNedslagsfeltBolk(final WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest wsFinnNAVKontorForGeografiskNedslagsfeltBolkRequest) throws FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput {
                final String geografiskNedslagsfelt = wsFinnNAVKontorForGeografiskNedslagsfeltBolkRequest.getGeografiskNedslagsfeltListe().get(0);
                return new WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse().withEnheterForGeografiskNedslagsfeltListe(
                        new WSEnheterForGeografiskNedslagsfelt().withGeografiskNedslagsfelt(geografiskNedslagsfelt).withEnhetListe(
                                Collections.singletonList(new WSDetaljertEnhet().withEnhetId(geografiskNedslagsfelt).withNavn("NAV Mockbrukers Enhet").withAntallRessurser(100).withStatus("AKTIV"))
                        ));
            }

            @Override
            public void ping() {

            }

            @Override
            public WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListe(final WSHentFullstendigEnhetListeRequest wsHentFullstendigEnhetListeRequest) {
                return null;
            }

            @Override
            public WSHentEnhetBolkResponse hentEnhetBolk(final WSHentEnhetBolkRequest wsHentEnhetBolkRequest) throws HentEnhetBolkUgyldigInput {
                return null;
            }
        };
    }

}
