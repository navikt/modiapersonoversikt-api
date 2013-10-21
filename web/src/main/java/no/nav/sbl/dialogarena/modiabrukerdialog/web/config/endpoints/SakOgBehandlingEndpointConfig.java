package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import no.nav.sbl.dialogarena.soknader.service.SoknaderService;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
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

import javax.jws.WebParam;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigInteger;

@Configuration
public class SakOgBehandlingEndpointConfig {

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
                public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) {
                    return new HentBehandlingskjedensBehandlingerResponse();
                }

                @Override
                public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) {
                    return new HentBehandlingResponse();
                }

                @Override
                public void ping() {
                }
            };
        }

    }

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
                    sak.setSaksId("id1");
                    sak.getBehandlingskjede().add(createBehandling1());
                    sak.getBehandlingskjede().add(createBehandling2());
                    response.withSak(sak);
                    return response;
                }

                private Behandlingskjede createBehandling1() {
                    try {
                        Behandlingskjede behandlingskjede = new Behandlingskjede();
                        behandlingskjede.withBehandlingskjedeId("behandling1");
                        behandlingskjede.withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.valueOf(10)).withType(new Behandlingstidtyper().withValue("dager")));
                        behandlingskjede.withStart(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
                        behandlingskjede.withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("Dagpenger"));
                        behandlingskjede.withSluttNAVtid(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
                        return behandlingskjede;
                    } catch (DatatypeConfigurationException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                private Behandlingskjede createBehandling2() {
                    try {
                        Behandlingskjede behandlingskjede = new Behandlingskjede();
                        behandlingskjede.withBehandlingskjedeId("behandling1");
                        behandlingskjede.withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.valueOf(10)).withType(new Behandlingstidtyper().withValue("dager")));
                        behandlingskjede.withStart(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar()));
                        behandlingskjede.withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("Uføre"));
                        return behandlingskjede;
                    } catch (DatatypeConfigurationException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) {
                    return new HentBehandlingskjedensBehandlingerResponse();
                }

                @Override
                public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) {
                    return new HentBehandlingResponse();
                }

                @Override
                public void ping() {
                }
            };
        }
    }
}
