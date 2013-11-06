package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;


import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.System.getProperties;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockSetupSingleton.mockSetup;

public class ConfigUtil {

    public static boolean isInMockMode(String key) {
        if (mockSetup().isTillat()) {
            String start = getProperties().getProperty(key);
            return start != null ? start.equalsIgnoreCase("yes") : false;
        }
        return false;
    }

    /**
     *  Gjør om en url-string til boolean. Url som "http://ja.nav.no" gir true, alle andre gir false.
     *
     */
    public static boolean transformUrlStringToBoolean(String url) {
        if(url == null) { return false; }

        try {
            URL boolURL = new URL(url);
            String host = boolURL.getHost();
            String[] boolString = host.split("\\.");
            return boolString.length > 1 && (boolString[0].equalsIgnoreCase("ja"));
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
