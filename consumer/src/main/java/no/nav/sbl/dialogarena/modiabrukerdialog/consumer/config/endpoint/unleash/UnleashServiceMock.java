package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.nav.modig.modia.ping.PingResult;

public class UnleashServiceMock implements UnleashService {
    @Override
    public boolean isEnabled(String toggleName) {
        return false;
    }

    @Override
    public PingResult ping() {
        return new PingResult(PingResult.ServiceResult.UNPINGABLE, 0);
    }

    @Override
    public String name() {
        return "Unleash Mock";
    }

    @Override
    public String method() {
        return "Mock";
    }

    @Override
    public String endpoint() {
        return "Mock";
    }
}
