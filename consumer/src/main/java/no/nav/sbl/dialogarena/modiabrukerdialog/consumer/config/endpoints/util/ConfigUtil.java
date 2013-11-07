package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;


import com.google.common.collect.Sets;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

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
     *  Gjør om en url-string til boolean.
     *  Url som inneholder ordet "ja" i hostnavnet gir true, alle andre gir false.
     *  F.eks  "http://ja.nav.no" gir true.
     *
     */
    public static boolean transformUrlStringToBoolean(String url) {
        if(url == null) { return false; }

        try {
            Set<String> set = Sets.newHashSet(new URL(url.toLowerCase()).getHost().split("\\."));
            return set.contains("ja");

        } catch (MalformedURLException e) {
            return false;
        }
    }
}
