package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;


import org.slf4j.Logger;

import static java.lang.System.getProperties;

public class ConfigUtil {

    public static boolean setUseMock(String key, Logger LOG) {
        String start = getProperties().getProperty(key);
        boolean useMock = false;
        if(start != null) {
            useMock = start.equalsIgnoreCase("no");
        } else {
            LOG.info("Kunne ikke lese start.properties");
        }
        return useMock;
    }
}
