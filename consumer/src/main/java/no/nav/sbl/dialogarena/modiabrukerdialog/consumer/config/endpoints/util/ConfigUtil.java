package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;


import static java.lang.System.getProperties;

public class ConfigUtil {

    public static boolean isInMockMode(String key) {
        String start = getProperties().getProperty(key);
        return start != null ? start.equalsIgnoreCase("yes") : false;
    }
}
