package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.getProperties;

public class ConfigUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    public static boolean isInMockMode(String key) {
        String start = getProperties().getProperty(key);
        boolean useMock = false;
        if (start != null) {
            useMock = start.equalsIgnoreCase("no");
        } else {
            LOG.info("Kunne ikke lese start.properties");
        }
        return useMock;
    }
}
