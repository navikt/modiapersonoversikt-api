package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.sbl.dialogarena.soknader.service.SoknaderServiceImpl;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingHentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.jws.WebParam;
import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.Utils.konfigurerMedHttps;

@Configuration
public class SoknaderConfig {

    @Profile({"default", "soknaderDefault"})
    @Configuration
    public static class Default {

        @Bean
        public SoknaderService soknaderWidgetService() {
            return new SoknaderServiceImpl();
        }

        @Bean
        public SakOgBehandlingPortType sakOgBehandlingPortType() {
            return new SakOgBehandlingPortType() {
                @Override
                public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                    return new FinnSakOgBehandlingskjedeListeResponse();
                }

                @Override
                public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                    return new HentBehandlingskjedensBehandlingerResponse();
                }

                @Override
                public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                    return new HentBehandlingResponse();
                }

                @Override
                public void ping() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            };
        }

    }

    @Profile({"test", "soknaderTest"})
    @Configuration
    public static class Test {

        @Bean
        public SoknaderService soknaderWidgetService() {
            return new SoknaderServiceImpl();
        }

        @Bean
        public SakOgBehandlingPortType sakOgBehandlingPortType() {
            return new SakOgBehandlingPortType() {
                @Override
                public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                    return new FinnSakOgBehandlingskjedeListeResponse();
                }

                @Override
                public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                    return new HentBehandlingskjedensBehandlingerResponse();
                }

                @Override
                public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                    return new HentBehandlingResponse();
                }

                @Override
                public void ping() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            };
        }
    }
}
