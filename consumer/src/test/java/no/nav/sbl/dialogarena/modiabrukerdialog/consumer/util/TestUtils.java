package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

public class TestUtils {
    public interface UnsafeRunneable {
        void call() throws Throwable;
    }

    public static void sneaky(UnsafeRunneable fn) {
        try {
            fn.call();
        } catch (Throwable ignored) {}
    }
}
