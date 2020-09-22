package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {
    @Autowired
    private StsConfig stsConfig;
    private static SelfTestCheck selftest = new SelfTestCheck(
            String.format("ArbeidOgAktivitet via %s", EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ARBEIDOGAKTIVITET_V1_ENDPOINTURL")),
            false,
            HealthCheckResult::healthy
    );

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        ArbeidOgAktivitet prod = createArbeidOgAktivitet();
        return createTimerProxyForWebService("ArbeidOgAktivitet", prod, ArbeidOgAktivitet.class);
    }

    private ArbeidOgAktivitet createArbeidOgAktivitet() {
        return new CXFClient<>(ArbeidOgAktivitet.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_ARBEIDOGAKTIVITET_V1_ENDPOINTURL"))
                .configureStsForSystemUser(stsConfig)
                .build();
    }

    @Bean
    public Pingable arbeidOgAktivitetPing(final ArbeidOgAktivitet ws) {
        return () -> selftest;
    }
}
