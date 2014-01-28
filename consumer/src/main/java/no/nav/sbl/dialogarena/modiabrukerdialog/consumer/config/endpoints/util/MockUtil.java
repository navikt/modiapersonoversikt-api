package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util;


import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.System.getProperty;

public class MockUtil {

    public static final String DEFAULT_MOCK_TILLATT = "nei";
    private static final String TILLATMOCKSETUP_URL = "tillatmocksetup.url";
    private static final String DEFAULT_MOCK_ALLOWANCE = "no";
    private static final String ALLOW_MOCK = "yes";

    public static boolean mockSetupErTillatt(String url) {
        if (url == null) { return false; }
        try {
            return newHashSet(new URL(url.toLowerCase()).getHost().split("\\.")).contains("ja");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean mockSetupErTillatt() {
        return mockSetupErTillatt(getProperty(TILLATMOCKSETUP_URL));
    }

    public static boolean mockErSlaattPaaForKey(String key) {
        return getProperty(key, DEFAULT_MOCK_ALLOWANCE).equalsIgnoreCase(ALLOW_MOCK);
    }

}
