package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;


import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.collect.Sets.newHashSet;

public class MockUtil {

    public static final String DEFAULT_MOCK_TILLATT = "nei";

    public static boolean mockSetupErTillatt(String url) {
        if(url == null) { return false; }
        try {
            return newHashSet(new URL(url.toLowerCase()).getHost().split("\\.")).contains("ja");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
