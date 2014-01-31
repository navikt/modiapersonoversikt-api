package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2PortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Value("${kodeverkendpoint.v2.url}")
    private URL endpoint;

    private KodeverkV2PortTypeImpl impl = new KodeverkV2PortTypeImpl(endpoint);
    private KodeverkV2PortTypeMock mock = new KodeverkV2PortTypeMock();

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        final KodeverkPortType portType = impl.kodeverkPortType();
        final KodeverkPortType portTypeMock = mock.kodeverkPortType();
        return new KodeverkPortType() {
            @Override
            public XMLHentKodeverkResponse hentKodeverk(@WebParam(name = "request", targetNamespace = "") XMLHentKodeverkRequest xmlHentKodeverkRequest)
                    throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KODEVERK_KEY)) {
                    return portTypeMock.hentKodeverk(xmlHentKodeverkRequest);
                }
                return portType.hentKodeverk(xmlHentKodeverkRequest);
            }

            @Override
            public XMLFinnKodeverkListeResponse finnKodeverkListe(@WebParam(name = "request", targetNamespace = "") XMLFinnKodeverkListeRequest xmlFinnKodeverkListeRequest) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KODEVERK_KEY)) {
                    return portTypeMock.finnKodeverkListe(xmlFinnKodeverkListeRequest);
                }
                return portType.finnKodeverkListe(xmlFinnKodeverkListeRequest);
            }

            @Override
            public void ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KODEVERK_KEY)) {
                    portTypeMock.ping();
                }
                portType.ping();
            }
        };
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        final KodeverkClient kodeverkKlient = impl.kodeverkClient();
        final KodeverkClient kodeverkKlientMock = mock.kodeverkClient();
        return new KodeverkClient() {
            @Override
            public XMLKodeverk hentKodeverk(String s) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KODEVERK_KEY)) {
                    return kodeverkKlientMock.hentKodeverk(s);
                }
                return kodeverkKlient.hentKodeverk(s);
            }

            @Override
            public String hentFoersteTermnavnForKode(String s, String s2) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KODEVERK_KEY)) {
                    return kodeverkKlientMock.hentFoersteTermnavnForKode(s, s2);
                }
                return kodeverkKlient.hentFoersteTermnavnForKode(s, s2);
            }
        };
    }
}
