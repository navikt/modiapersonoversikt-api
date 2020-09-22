package no.nav.metrics;

import no.nav.common.metrics.Event;
import no.nav.common.metrics.MetricsClient;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class Timer extends Event {
    private final String name;
    private final Timing timing;
    private final MetricsClient metricsClient;
    /*
        Bruker både measureTimestamp og startTime fordi System.nanoTime()
        skal brukes for tidsmåling og System.currentTimeMillis() for å
        rapportere når målingen ble gjort.
     */
    private long measureTimestamp;
    private long startTime;
    private long stopTime;

    Timer(MetricsClient metricsClient, String name, Timing timing) {
        super(name);
        this.metricsClient = metricsClient;
        this.name = name;
        this.timing = timing;
    }

    public Timer start() {
        measureTimestamp = timing.currentTimeMillis();
        startTime = timing.nanoTime();
        return this;
    }

    public Timer stop() {
        stopTime = timing.nanoTime();
        addFieldToReport("value", getElpasedTimeInMillis());
        return this;
    }

    long getElpasedTimeInMillis() {
        long elapsedTimeNanos = stopTime - startTime;

        return NANOSECONDS.toMillis(elapsedTimeNanos);
    }

    public Timer report() {
        ensureTimerIsStopped();
        metricsClient.report(this.name, this.getFields(), this.getTags(), measureTimestamp);
        reset();
        return this;
    }

    private void ensureTimerIsStopped() {
        if (!getFields().containsKey("value")) {
            throw new IllegalStateException("Must stop timer before reporting!");
        }
    }

    /**
     * Timer er ikke threadsafe, bruk en ny timer heller enn å resette en eksisterende
     * om flere tråder kan aksessere målepunktet samtidig
     */
    private void reset() {
        measureTimestamp = 0;
        startTime = 0;
        stopTime = 0;
        getFields().clear();
    }
}
