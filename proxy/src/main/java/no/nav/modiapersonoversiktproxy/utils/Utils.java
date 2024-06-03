package no.nav.modiapersonoversiktproxy.utils;

import java.util.Map;
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

    public static <T> T withProperties(Map<String, String> properties, Supplier<T> fn) {
        try {
            for (var property : properties.entrySet()) {
                System.setProperty(property.getKey(), property.getValue());
            }
            return fn.get();
        } finally {
            for (var property : properties.entrySet()) {
                System.clearProperty(property.getKey());
            }
        }
    }
}
