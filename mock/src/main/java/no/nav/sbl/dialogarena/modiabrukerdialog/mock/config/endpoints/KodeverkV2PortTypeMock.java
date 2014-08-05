package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KodeverkV2PortTypeMock {

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        return new KodeverkPortType() {
            @Override
            public XMLHentKodeverkResponse hentKodeverk(XMLHentKodeverkRequest request) throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
                return new XMLHentKodeverkResponse();
            }

            @Override
            public XMLFinnKodeverkListeResponse finnKodeverkListe(XMLFinnKodeverkListeRequest request) {
                return new XMLFinnKodeverkListeResponse();
            }

            @Override
            public void ping() {
                return;
            }
        };
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        return mock(KodeverkClient.class);
    }

}
