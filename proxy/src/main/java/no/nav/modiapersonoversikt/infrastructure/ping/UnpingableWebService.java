package no.nav.modiapersonoversikt.infrastructure.ping;

import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;

public class UnpingableWebService implements Pingable {
    private final SelfTestCheck instance;

    public UnpingableWebService(String name, String address) {
        assert name.length() > 0;
        this.instance = new SelfTestCheck(
                String.format("%s via %s", name, address),
                false,
                HealthCheckResult::healthy
        );
    }

    @Override
    public SelfTestCheck ping() {
        return instance;
    }
}
