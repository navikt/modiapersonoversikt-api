package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
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
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigInteger;
import java.net.URL;

@Configuration
public class SakOgBehandlingEndpointConfig {

    @Configuration
    public static class Default {

        @Value("${sakogbehandling.ws.url}")
        private URL sakogbehandlingEndpoint;

        @Bean
        public SakOgBehandlingPortType sakOgBehandlingPortType() {
            SakOgBehandlingPortType sakOgBehandlingPortType = createSakOgBehandlingPortType(new UserSAMLOutInterceptor());
//            STSConfigurationUtility.configureStsForExternalSSO(getClient(sakOgBehandlingPortType));
            return sakOgBehandlingPortType;
        }

        @Bean
        public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
            SakOgBehandlingPortType sakOgBehandlingPortType = createSakOgBehandlingPortType(new UserSAMLOutInterceptor());
//            STSConfigurationUtility.configureStsForSystemUser(getClient(sakOgBehandlingPortType));
            return sakOgBehandlingPortType;
        }


        private SakOgBehandlingPortType createSakOgBehandlingPortType(AbstractSAMLOutInterceptor interceptor){
            JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
            proxyFactoryBean.setWsdlLocation("sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/SakOgBehandling.wsdl");
            proxyFactoryBean.setAddress(sakogbehandlingEndpoint.toString());
            proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
            proxyFactoryBean.getOutInterceptors().add(interceptor);
            proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
            proxyFactoryBean.getFeatures().add(new LoggingFeature());
//            proxyFactoryBean.getFeatures().add(new TimeoutFeature());
            return proxyFactoryBean.create(SakOgBehandlingPortType.class);
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
                        behandlingskjede.withBehandlingskjedetype(new Behandlingskjedetyper().withKodeverksRef("Dagpenger"));
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
                        behandlingskjede.withBehandlingskjedetype(new Behandlingskjedetyper().withKodeverksRef("Uføre"));
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
