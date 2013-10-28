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
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        proxyFactoryBean.setWsdlLocation("utbetaling/no/nav/virksomhet/tjenester/utbetaling/utbetaling.wsdl");
        proxyFactoryBean.setAddress(utbetalingEndpoint.toString());
        proxyFactoryBean.setServiceClass(Utbetaling.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        final Utbetaling utbetalingTjeneste = proxyFactoryBean.create(Utbetaling.class);
        return new UtbetalingPortType() {
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                return utbetalingTjeneste.hentUtbetalingListe(request);
            }
        };

    }


}
