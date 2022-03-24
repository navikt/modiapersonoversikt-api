package no.nav.modiapersonoversikt.consumer.arena.arbeidogaktivitet;

import no.nav.common.cxf.CXFClient;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.modiapersonoversikt.utils.Utils.withProperty;

@Configuration
public class ArbeidOgAktivitetConfig {

    private static String address = EnvironmentUtils.getRequiredProperty("TJENESTEBUSS_URL") + "nav-tjeneste-arbeidOgAktivitet_v1Web/sca/ArbeidOgAktivitetWSEXP";
    public static final String KJERNEINFO_TJENESTEBUSS_USERNAME = "SRV_KJERNEINFO_TJENESTEBUSS_USERNAME";
    public static final String KJERNEINFO_TJENESTEBUSS_PASSWORD = "SRV_KJERNEINFO_TJENESTEBUSS_PASSWORD";

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        ArbeidOgAktivitet prod = createArbeidOgAktivitet();
        return createTimerProxyForWebService("ArbeidOgAktivitet", prod, ArbeidOgAktivitet.class);
    }

    @Bean
    public Pingable arbeidOgAktivitetPing(final ArbeidOgAktivitet ws) {
        SelfTestCheck selftest = new SelfTestCheck(
                String.format("ArbeidOgAktivitet via %s", address),
                false,
                () -> {
                    try {
                        ws.hentSakListe(new WSHentSakListeRequest()
                                .withBruker(new WSBruker().withBrukertypeKode("PERSON").withBruker("10108000398"))
                                .withFom(LocalDate.now())
                                .withTom(LocalDate.now())
                        );
                        return HealthCheckResult.healthy();
                    } catch (Exception e) {
                        return HealthCheckResult.unhealthy(e);
                    }
                }
        );
        return () -> selftest;
    }

    private ArbeidOgAktivitet createArbeidOgAktivitet() {
        return withProperty("disable.ssl.cn.check", "true", () -> new CXFClient<>(ArbeidOgAktivitet.class)
                .address(address)
                .wsdl("classpath:wsdl/nav-tjeneste-arbeidOgAktivitet_ArbeidOgAktivitetWSEXP.wsdl")
                .serviceName(new QName("http://nav.no/virksomhet/tjenester/sak/arbeidogaktivitet/v1/Binding", "ArbeidOgAktivitetWSEXP_ArbeidOgAktivitetHttpService"))
                .endpointName(new QName("http://nav.no/virksomhet/tjenester/sak/arbeidogaktivitet/v1/Binding", "ArbeidOgAktivitetWSEXP_ArbeidOgAktivitetHttpPort"))
                .withOutInterceptor(new WSS4JOutInterceptor(getSecurityProps()))
                .build());
    }

    private static Map<String, Object> getSecurityProps() {
        final String user = EnvironmentUtils.getRequiredProperty("ctjenestebuss.username", KJERNEINFO_TJENESTEBUSS_USERNAME);
        final String password = EnvironmentUtils.getRequiredProperty("ctjenestebuss.password", KJERNEINFO_TJENESTEBUSS_PASSWORD);

        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.USER, user);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_REF, (CallbackHandler) callbacks -> {
            WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];
            passwordCallback.setPassword(password);
        });
        return props;
    }
}
