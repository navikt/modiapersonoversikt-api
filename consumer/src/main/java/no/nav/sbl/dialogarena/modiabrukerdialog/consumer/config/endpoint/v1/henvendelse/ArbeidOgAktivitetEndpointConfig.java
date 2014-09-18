package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelse;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.ArbeidOgAktivitetMock.createArbeidOgAktivitetMock;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {

    public static final String ARENA_KEY = "start.arena.arbeidOgAktivitet.withmock";

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        return createSwitcher(
                createArbeidOgAktivitetPortType(new UserSAMLOutInterceptor()),
                createArbeidOgAktivitetMock(),
                ARENA_KEY,
                ArbeidOgAktivitet.class
        );
    }

    @Bean
    public Pingable arbeidOgAktivitetPing() {
        final ArbeidOgAktivitet ws = createArbeidOgAktivitetPortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "ARENA_ARBEIDOGAKTIVITET_V1";
                try {
                    ws.hentSakListe(new WSHentSakListeRequest().withBruker(new WSBruker().withBruker("10108000398")));
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static ArbeidOgAktivitet createArbeidOgAktivitetPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(System.getProperty("arbeidOgAktivitet.v1.url"));
        proxyFactoryBean.setWsdlLocation("classpath:arbeid/nav/tjeneste/arbeidOgAktivitet/ArbeidOgAktivitetWSEXP.wsdl");
        proxyFactoryBean.setServiceClass(ArbeidOgAktivitet.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        return proxyFactoryBean.create(ArbeidOgAktivitet.class);
    }

}
