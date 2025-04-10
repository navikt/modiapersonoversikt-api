package no.nav.modiapersonoversikt.infrastructure.ping;

import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult;
import no.nav.common.health.selftest.SelfTestCheck;

import java.io.File;

public class DiskCheck implements HealthCheck {
    private static final DiskCheck INSTANCE = new DiskCheck();
    private static final long LIMIT = 300_000_000L;
    private static final File DISK = new File(".").getAbsoluteFile();

    @Override
    public HealthCheckResult checkHealth() {
        long freeSpace = DISK.getFreeSpace();
        if (freeSpace > LIMIT) {
            return HealthCheckResult.healthy();
        }
        return HealthCheckResult.unhealthy(
                String.format("Mindre enn %s MB ledig diskplass for %s", LIMIT / 1_000_000, DISK)
        );
    }

    public static SelfTestCheck asSelftestCheck() {
        return new SelfTestCheck(
                String.format("Sjekk for om det er mindre enn %s MB diskplass ledig", LIMIT / 1_000_000),
                false,
                INSTANCE
        );
    }
}
