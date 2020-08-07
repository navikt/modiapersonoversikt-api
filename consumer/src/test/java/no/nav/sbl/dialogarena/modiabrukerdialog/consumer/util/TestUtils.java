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

    public static void withEnv(String name, String value, UnsafeRunneable fn) {
        String original = System.getProperty(name, value);
        System.setProperty(name, value);
        sneaky(fn);
        System.setProperty(name, original);
    }
}
