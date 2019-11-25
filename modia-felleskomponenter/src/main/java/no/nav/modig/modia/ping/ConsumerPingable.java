package no.nav.modig.modia.ping;

import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.fn.UnsafeRunnable;

public class ConsumerPingable<T> implements Pingable {
    private final UnsafeRunnable ping;
    private final Ping.PingMetadata metadata;

    public ConsumerPingable(String name, UnsafeRunnable ping) {
        this(name, false,  ping);
    }

    public ConsumerPingable(String name, boolean kritisk, UnsafeRunnable ping) {
        this(new Ping.PingMetadata(name, "", "", kritisk), ping);
    }

    public ConsumerPingable(Ping.PingMetadata metadata, UnsafeRunnable ping) {
        this.ping = ping;
        this.metadata = metadata;
    }


    @Override
    public Ping ping() {
        try {
            ping.runUnsafe();
            return Ping.lyktes(metadata);
        } catch (Throwable e) {
            return Ping.feilet(metadata, e);
        }
    }

    public interface UnsafeConsumer<T> {
        void accept(T t) throws Throwable;
    }
}
