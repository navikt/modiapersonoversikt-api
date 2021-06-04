package no.nav.modiapersonoversikt.infrastructure.ping;

import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;

import java.lang.reflect.InvocationTargetException;

public class ConsumerPingable implements Pingable {
    private final UnsafeRunnable ping;
    private final SelfTestCheck instance;

    public ConsumerPingable(String description, UnsafeRunnable ping) {
        this(description, false,  ping);
    }

    public ConsumerPingable(String description, boolean kritisk, UnsafeRunnable ping) {
        this.ping = ping;
        this.instance = new SelfTestCheck(
                description,
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
            if (e instanceof InvocationTargetException) {
                return HealthCheckResult.unhealthy(((InvocationTargetException) e).getTargetException());
            }
            return HealthCheckResult.unhealthy(e);
        }
    }

    public interface UnsafeConsumer<T> {
        void accept(T t) throws Throwable;
    }
}
