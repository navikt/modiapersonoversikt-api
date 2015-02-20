package no.nav.sbl.dialogarena.utbetaling.config;

import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.getWsUtbetalinger;

@Configuration
public class UtbetalingPortTypeTestConfig {

    private static final String FNR = "12345612345";

    @Bean
    public UtbetalingV1 utbetalingPortType() {
        if(valueOf(getProperty("utbetal.endpoint.mock", "true"))) {
            return getUtbetalingV1Stub();
        }
        return createUtbetalingPortType();
    }

    protected UtbetalingV1 getUtbetalingV1Stub() {
        return new UtbetalingV1() {
            @Override
            public void ping() {

            }

            @Override
            public WSHentUtbetalingsinformasjonResponse hentUtbetalingsinformasjon(WSHentUtbetalingsinformasjonRequest request) throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
                String ident = request.getId().getIdent();
                if(ident == null) {
                    ident = FNR;
                }
                return new WSHentUtbetalingsinformasjonResponse().withUtbetalingListe(getWsUtbetalinger(ident, request.getPeriode().getFom(), request.getPeriode().getTom()));
            }
        };
    }

    private UtbetalingV1 createUtbetalingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:utbetaling/no/nav/tjeneste/virksomhet/utbetaling/v1/Binding.wsdl");
        proxyFactoryBean.setAddress(getProperty("utbetalingendpoint.url"));
        proxyFactoryBean.setServiceClass(UtbetalingV1.class);
        proxyFactoryBean.setServiceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"));
        proxyFactoryBean.setEndpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"));
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        return proxyFactoryBean.create(UtbetalingV1.class);
    }

}
