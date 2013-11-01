package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.SakOgBehandlingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingHentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.ConfigUtil.isInMockMode;

@Configuration
public class SakOgBehandlingEndpointConfig {

    @Value("${sakogbehandling.url}")
    private URL sakogbehandlingEndpoint;
    private boolean useMock;
    private SakOgBehandlingPortTypeImpl portType = new SakOgBehandlingPortTypeImpl();
    private SakOgBehandlingPortTypeMock portTypeMock = new SakOgBehandlingPortTypeMock();

    public SakOgBehandlingEndpointConfig() {
        useMock = isInMockMode("start.sakogbehandling.withintegration");
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        if (useMock) {
            return portTypeMock.sakOgBehandlingPortType();
        }
        SakOgBehandlingPortType sakOgBehandlingPortType = portType.sakOgBehandlingPortType(sakogbehandlingEndpoint);
        return createSakOgBehandlingPortType(sakOgBehandlingPortType);
    }

    @Bean
    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
        if (useMock) {
            return portTypeMock.sakOgBehandlingPortType();
        }
        SakOgBehandlingPortType sakOgBehandlingPortType = portType.selfTestSakOgBehandlingPortType(sakogbehandlingEndpoint);
        return createSakOgBehandlingPortType(sakOgBehandlingPortType);
    }

    private SakOgBehandlingPortType createSakOgBehandlingPortType(final SakOgBehandlingPortType sakOgBehandlingPortType) {
        return new SakOgBehandlingPortType() {

            @Cacheable("endpointCache")
            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                return sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(finnSakOgBehandlingskjedeListeRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                return sakOgBehandlingPortType.hentBehandlingskjedensBehandlinger(hentBehandlingskjedensBehandlingerRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                return sakOgBehandlingPortType.hentBehandling(hentBehandlingRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public void ping() {
                sakOgBehandlingPortType.ping();
            }
        };
    }


}
