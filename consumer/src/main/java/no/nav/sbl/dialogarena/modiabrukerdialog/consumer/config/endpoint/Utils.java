package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import java.util.function.Supplier;

public class Utils {
    public static <T> T withProperty(String key, String value, Supplier<T> fn) {
        String original = System.getProperty(key);
        try {
            System.setProperty(key, value);
            return fn.get();
        } finally {
            if (original == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, original);
            }
        }
    }
}
