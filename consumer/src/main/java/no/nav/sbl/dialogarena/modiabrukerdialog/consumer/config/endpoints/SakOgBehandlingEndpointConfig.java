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

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.ConfigUtil.isInMockMode;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class SakOgBehandlingEndpointConfig {

    @Value("${sakogbehandling.url}")
    private URL sakogbehandlingEndpoint;


    private SakOgBehandlingPortType portType;
    private SakOgBehandlingPortType portTypeSelftTest;
    private SakOgBehandlingPortType portTypeMock = new SakOgBehandlingPortTypeMock().sakOgBehandlingPortType();
    String key = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        portType = new SakOgBehandlingPortTypeImpl().sakOgBehandlingPortType(sakogbehandlingEndpoint);
        SakOgBehandlingPortType switcher = createSwitcher(portType, portTypeMock, key, SakOgBehandlingPortType.class);
        return createSakOgBehandlingPortType(switcher, portTypeMock);
    }

    @Bean
    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
        portTypeSelftTest = new SakOgBehandlingPortTypeImpl().selfTestSakOgBehandlingPortType(sakogbehandlingEndpoint);
        SakOgBehandlingPortType switcher = createSwitcher(portTypeSelftTest, portTypeMock, key, SakOgBehandlingPortType.class);
        return createSakOgBehandlingPortType(switcher, portTypeMock);
    }

    private SakOgBehandlingPortType createSakOgBehandlingPortType(final SakOgBehandlingPortType portType, final SakOgBehandlingPortType portTypeMock) {
        return new SakOgBehandlingPortType() {

            @Cacheable("endpointCache")
            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                if (isInMockMode(key)) {
                    return portTypeMock.finnSakOgBehandlingskjedeListe(finnSakOgBehandlingskjedeListeRequest);
                }
                return portType.finnSakOgBehandlingskjedeListe(finnSakOgBehandlingskjedeListeRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                if (isInMockMode(key)) {
                    return portTypeMock.hentBehandlingskjedensBehandlinger(hentBehandlingskjedensBehandlingerRequest);
                }
                return portType.hentBehandlingskjedensBehandlinger(hentBehandlingskjedensBehandlingerRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                if (isInMockMode(key)) {
                    return portTypeMock.hentBehandling(hentBehandlingRequest);
                }
                return portType.hentBehandling(hentBehandlingRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public void ping() {
                if (isInMockMode(key)) {
                    portTypeMock.ping();
                    return;
                }
                portType.ping();
            }
        };
    }


}
