package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling;


import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.cache.annotation.Cacheable;

import java.net.URL;

public class UtbetalingPortTypeImpl {

    private URL endpoint;

    public UtbetalingPortTypeImpl(URL endpoint) {
        this.endpoint = endpoint;
    }

    public UtbetalingV1 utbetalingPortType() {
        return createUtbetalingPortType(new UserSAMLOutInterceptor());
    }

    private UtbetalingV1 createUtbetalingPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("utbetaling/no/nav/virksomhet/tjenester/utbetaling/utbetaling.wsdl");
        proxyFactoryBean.setAddress(endpoint.toString());
        proxyFactoryBean.setServiceClass(UtbetalingV1.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        final UtbetalingV1 utbetalingTjeneste = proxyFactoryBean.create(UtbetalingV1.class);
        return new UtbetalingV1() {

            @Override
            public void ping() {

            }

            @Cacheable("endpointCache")
            @Override
            public WSHentUtbetalingsinformasjonResponse hentUtbetalingsinformasjon(WSHentUtbetalingsinformasjonRequest request) throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
                return utbetalingTjeneste.hentUtbetalingsinformasjon(request);
            }
        };

    }
}
