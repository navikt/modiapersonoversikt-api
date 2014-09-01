package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.sakogbehandling;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class SakOgBehandlingEndpointConfig {

    public static final String SAKOGBEHANDLING_KEY = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        final SakOgBehandling_v1PortType prod = createSakogbehandlingPortType();
        final SakOgBehandling_v1PortType mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();
        return new SakOgBehandling_v1PortType() {
            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest request) {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    return mock.finnSakOgBehandlingskjedeListe(request);
                }
                return prod.finnSakOgBehandlingskjedeListe(request);
            }

            @Override
            public void ping() {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    mock.ping();
                } else {
                    prod.ping();
                }
            }

            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger
                    (@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest)
                    throws HentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                throw new RuntimeException("skal ikke brukes");
            }

            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingBehandlingIkkeFunnet {
                throw new RuntimeException("skal ikke brukes");
            }
        };
    }

    private SakOgBehandling_v1PortType createSakogbehandlingPortType() {
        return new CXFClient<>(SakOgBehandling_v1PortType.class)
                .address(System.getProperty("sakogbehandling.ws.url"))
                .wsdl("classpath:sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/sakOgBehandling.wsdl")
                .build();
    }

}
