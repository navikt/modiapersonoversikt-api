package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSEnhetsstatus;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
public class OrganisasjonEnhetV2Mock {

    @Bean
    public static OrganisasjonEnhetV2 organisasjonEnhetV2() {
        return new OrganisasjonEnhetV2() {
            @Override
            public void ping() {

            }

            @Override
            public WSHentFullstendigEnhetListeResponse hentFullstendigEnhetListe(
                    final WSHentFullstendigEnhetListeRequest wsHentFullstendigEnhetListeRequest) {
                return new WSHentFullstendigEnhetListeResponse().withEnhetListe(lagWSDetaljertEnhet());
            }

            @Override
            public WSHentEnhetBolkResponse hentEnhetBolk(final WSHentEnhetBolkRequest wsHentEnhetBolkRequest) {
                return new WSHentEnhetBolkResponse().withEnhetListe(lagWSDetaljertEnhet());
            }

            @Override
            public WSHentOverordnetEnhetRequest hentOverordnetEnhet(WSHentOverordnetEnhetRequest wsHentOverordnetEnhetRequest) throws HentOverordnetEnhetEnhetIkkeFunnet, HentOverordnetEnhetEnhetenErVirtuell {
                throw new NotImplementedException();
            }

            @Override
            public WSFinnNAVKontorResponse finnNAVKontor(WSFinnNAVKontorRequest wsFinnNAVKontorRequest) throws FinnNAVKontorUgyldigInput {
                throw new NotImplementedException();
            }

            private List<WSOrganisasjonsenhet> lagWSDetaljertEnhet() {
                final List<WSOrganisasjonsenhet> enheter = new ArrayList<>();
                IntStream.rangeClosed(101, 200).forEach(i -> enheter.add(lagWSDetaljertEnhet(i)));
                enheter.add(new WSOrganisasjonsenhet()
                        .withEnhetId("300")
                        .withStatus(WSEnhetsstatus.AKTIV)
                        .withEnhetNavn("Enhet uten saksbehandlere"));
                return enheter;
            }

            private WSOrganisasjonsenhet lagWSDetaljertEnhet(final int enhetId) {
                return new WSOrganisasjonsenhet()
                        .withEnhetId(StringUtils.leftPad(String.valueOf(enhetId), 4, '0'))
                        .withEnhetNavn("NAV Mockbrukers Enhet")
                        .withStatus(WSEnhetsstatus.AKTIV);
            }
        };
    }

}
