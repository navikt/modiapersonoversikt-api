package no.nav.modiapersonoversikt.utils;

import javax.xml.soap.SOAPFault;

public class TestUtils {
    public interface UnsafeRunneable {
        void call() throws Throwable;
    }

    public static void sneaky(UnsafeRunneable fn) {
        try {
            fn.call();
        } catch (AssertionError assertion) {
            throw assertion;
        } catch (Throwable ignored) {
        }
    }

    public static void withEnv(String name, String value, UnsafeRunneable fn) {
        String original = System.getProperty(name, value);
        System.setProperty(name, value);
        sneaky(fn);
        System.setProperty(name, original);
    }
}
