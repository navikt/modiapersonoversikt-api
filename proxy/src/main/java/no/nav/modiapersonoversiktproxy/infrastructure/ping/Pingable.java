package no.nav.modiapersonoversiktproxy.infrastructure.ping;

import no.nav.common.health.selftest.SelfTestCheck;

public interface Pingable {
    SelfTestCheck ping();
}
