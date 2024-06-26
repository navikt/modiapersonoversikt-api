package no.nav.modiapersonoversiktproxy.infrastructure.metrics;

interface Timing {
    default long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    default long nanoTime() {
        return System.nanoTime();
    }
}
