package no.nav.modig.modia.ping;

import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.sbl.dialogarena.types.Pingable;

public class ConsumerPingable implements Pingable {
    private final UnsafeRunnable ping;
    private final SelfTestCheck instance;

    public ConsumerPingable(String name, UnsafeRunnable ping) {
        this(name, false,  ping);
    }

    public ConsumerPingable(String name, boolean kritisk, UnsafeRunnable ping) {
        this.ping = ping;
        this.instance = new SelfTestCheck(
                name,
                kritisk,
                this::check
        );
    }

    @Override
    public SelfTestCheck ping() {
        return instance;
    }

    private HealthCheckResult check() {
        try {
            ping.runUnsafe();
            return HealthCheckResult.healthy();
        } catch (Throwable e) {
            return HealthCheckResult.unhealthy(e);
        }
    }

    public interface UnsafeConsumer<T> {
        void accept(T t) throws Throwable;
    }
}
