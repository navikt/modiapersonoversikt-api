package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles;

import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingHentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.jws.WebParam;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.math.BigInteger;

@Configuration
public class SoknaderConfig {

    @Profile({"default", "soknaderDefault"})
    @Configuration
    public static class Default {

        @Bean
        public SoknaderService soknaderWidgetService() {
            return new SoknaderService();
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
            return new SoknaderService();
        }

        @Bean
        public SakOgBehandlingPortType sakOgBehandlingPortType() {
            return new SakOgBehandlingPortType() {
                @Override
                public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                    FinnSakOgBehandlingskjedeListeResponse response = new FinnSakOgBehandlingskjedeListeResponse();
                    Sak sak = new Sak();
                    sak.setTema(new Temaer().withKodeRef("Dagpenger"));
                    try {
                        sak.setOpprettet(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
                        sak.setLukket(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
                    sak.setSaksId("id1");
                    Behandlingskjede behandlingskjede = new Behandlingskjede();
                    behandlingskjede.withBehandlingskjedeId("behndling1");
                    behandlingskjede.withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.valueOf(10)));
                    behandlingskjede.withStart(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
                    sak.withBehandlingskjede(behandlingskjede);
                    response.withSak(sak);
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    return response;
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
