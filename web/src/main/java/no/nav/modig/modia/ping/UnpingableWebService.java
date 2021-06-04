package no.nav.modig.modia.ping;

import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.sbl.dialogarena.types.Pingable;

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
