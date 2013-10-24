package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;


import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.net.URL;

@Configuration
public class UtbetalingEndpointConfig {
    @Value("${utbetaling.url}")
    private URL utbetalingEndpoint;

    @Bean
    public UtbetalingPortType utbetalingPortType() {
        return createUtbetalingPortType(new UserSAMLOutInterceptor());
    }

    private UtbetalingPortType createUtbetalingPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("utbetaling/no/nav/virksomhet/tjenester/utbetaling/Utbetaling.wsdl");
        proxyFactoryBean.setAddress(utbetalingEndpoint.toString());
        proxyFactoryBean.setServiceClass(Utbetaling.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        return new UtbetalingPortType(proxyFactoryBean.create(Utbetaling.class));

    }

    private class UtbetalingPortType {
        private Utbetaling utbetalingTjeneste;

        public UtbetalingPortType(Utbetaling utbetalingTjeneste) {
            this.utbetalingTjeneste = utbetalingTjeneste;
        }

        public WSHentUtbetalingListeResponse hentUtbetalingListe(@WebParam(name = "request", targetNamespace = "") WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
            return utbetalingTjeneste.hentUtbetalingListe(request);
        }
    }


}
