package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.sakogbehandling;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class SakOgBehandlingEndpointConfig {

    public static final String SAKOGBEHANDLING_KEY = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        final SakOgBehandlingPortType prod = null;
        final SakOgBehandlingPortType mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();
        return new SakOgBehandlingPortType() {
            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest request) {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    return mock.finnSakOgBehandlingskjedeListe(request);
                }
                return prod.finnSakOgBehandlingskjedeListe(request);
            }

            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest request)
                    throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    return mock.hentBehandlingskjedensBehandlinger(request);
                }
                return prod.hentBehandlingskjedensBehandlinger(request);
            }

            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest request) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    return mock.hentBehandling(request);
                }
                return prod.hentBehandling(request);
            }

            @Override
            public void ping() {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    mock.ping();
                } else {
                    prod.ping();
                }
            }
        };
    }

}
