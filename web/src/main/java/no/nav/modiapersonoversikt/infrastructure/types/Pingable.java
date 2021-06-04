package no.nav.modiapersonoversikt.infrastructure.types;

import no.nav.common.health.selftest.SelfTestCheck;

public interface Pingable {
    SelfTestCheck ping();
}
