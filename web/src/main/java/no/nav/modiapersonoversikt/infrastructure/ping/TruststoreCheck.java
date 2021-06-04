package no.nav.modiapersonoversikt.infrastructure.ping;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.common.utils.SslUtils;

import java.util.Optional;

public class TruststoreCheck implements HealthCheck {
    private static TruststoreCheck INSTANCE = new TruststoreCheck();

    @Override
    public HealthCheckResult checkHealth() {
        Optional<String> truststore = EnvironmentUtils
                .getOptionalProperty(SslUtils.JAVAX_NET_SSL_TRUST_STORE);

        return truststore
                .map((value) -> HealthCheckResult.healthy())
                .orElseGet(() -> HealthCheckResult.unhealthy(truststore.orElse("N/A")));
    }

    public static SelfTestCheck asSelftestCheck() {
        return new SelfTestCheck(
                "Sjekker at truststore er satt",
                true,
                INSTANCE
        );
    }
}
