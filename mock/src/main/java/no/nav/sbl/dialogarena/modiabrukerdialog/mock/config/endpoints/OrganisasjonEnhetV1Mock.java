package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.IntStream;

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
            public WSFinnArbeidsfordelingForEnhetBolkResponse finnArbeidsfordelingForEnhetBolk(WSFinnArbeidsfordelingForEnhetBolkRequest wsFinnArbeidsfordelingForEnhetBolkRequest) throws FinnArbeidsfordelingForEnhetBolkUgyldigInput {
                String enhetIdFraRequest = wsFinnArbeidsfordelingForEnhetBolkRequest.getKriterierListe().get(0).getEnhetId();
                WSArbeidsfordelingerForEnhet arbeidsfordelingerForEnhet = new WSArbeidsfordelingerForEnhet()
                        .withEnhetId(enhetIdFraRequest)
                        .withArbeidsfordelingskriterier(new WSArbeidsfordelingskriterier()
                                .withGeografiskNedslagsfelt(enhetIdFraRequest));
                
                return new WSFinnArbeidsfordelingForEnhetBolkResponse()
                        .withArbeidsfordelingerForEnhetListe(arbeidsfordelingerForEnhet);
            }

            @Override
            public WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse finnNAVKontorForGeografiskNedslagsfeltBolk(final WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest wsFinnNAVKontorForGeografiskNedslagsfeltBolkRequest) throws FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput {
                final String geografiskNedslagsfelt = wsFinnNAVKontorForGeografiskNedslagsfeltBolkRequest.getGeografiskNedslagsfeltListe().get(0);
                return new WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse().withEnheterForGeografiskNedslagsfeltListe(
                        new WSEnheterForGeografiskNedslagsfelt().withGeografiskNedslagsfelt(geografiskNedslagsfelt).withEnhetListe(
                                Collections.singletonList(lagWSDetaljertEnhet(Integer.valueOf(geografiskNedslagsfelt)))
                        ));
            }

            @Override
            public void ping() {

            }

            @Override
            public WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListe(final WSHentFullstendigEnhetListeRequest wsHentFullstendigEnhetListeRequest) {
                return new WSHentFullstendigEnhetListeResponse().withEnhetListe(lagWSDetaljertEnhet());
            }

            @Override
            public WSHentEnhetBolkResponse hentEnhetBolk(final WSHentEnhetBolkRequest wsHentEnhetBolkRequest) throws HentEnhetBolkUgyldigInput {
                return new WSHentEnhetBolkResponse().withEnhetListe(lagWSDetaljertEnhet());
            }

            private List<WSDetaljertEnhet> lagWSDetaljertEnhet() {
                final List<WSDetaljertEnhet> enheter = new ArrayList<>();
                IntStream.rangeClosed(101, 200).forEach(i -> enheter.add(lagWSDetaljertEnhet(i)));
                enheter.add(new WSDetaljertEnhet().withEnhetId("300").withAntallRessurser(0).withNavn("Enhet uten saksbehandlere").withStatus("AKTIV"));
                return enheter;
            }

            private WSDetaljertEnhet lagWSDetaljertEnhet(final int enhetId) {
                return new WSDetaljertEnhet().withEnhetId(StringUtils.leftPad(String.valueOf(enhetId), 4, '0'))
                        .withNavn("NAV Mockbrukers Enhet").withAntallRessurser(100).withStatus("AKTIV");
            }
        };
    }

}
