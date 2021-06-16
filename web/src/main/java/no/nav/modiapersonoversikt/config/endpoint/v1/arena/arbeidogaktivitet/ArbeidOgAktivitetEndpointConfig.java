package no.nav.modiapersonoversikt.config.endpoint.v1.arena.arbeidogaktivitet;

import no.nav.common.cxf.CXFClient;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;
import static no.nav.modiapersonoversikt.config.endpoint.v1.norg.NorgEndpointFelles.getSecurityProps;
import static no.nav.modiapersonoversikt.config.endpoint.Utils.withProperty;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {

    private static String address = EnvironmentUtils.getRequiredProperty("TJENESTEBUSS_URL") + "nav-tjeneste-arbeidOgAktivitet_v1Web/sca/ArbeidOgAktivitetWSEXP";

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        ArbeidOgAktivitet prod = createArbeidOgAktivitet();
        return createTimerProxyForWebService("ArbeidOgAktivitet", prod, ArbeidOgAktivitet.class);
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
}
