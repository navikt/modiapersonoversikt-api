package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util;


import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;

public class MockUtil {

    public static final String TILLATMOCKSETUP_PROPERTY = "tillatmocksetup";
    public static final String DEFAULT_MOCK_TILLATT = "false";
    public static final String ALLOW_MOCK = "true";

    public static boolean mockSetupErTillatt() {
        return valueOf(getProperty(TILLATMOCKSETUP_PROPERTY, DEFAULT_MOCK_TILLATT));
    }

    public static boolean mockErTillattOgSlaattPaaForKey(String key) {
        return mockSetupErTillatt() && getProperty(key, DEFAULT_MOCK_TILLATT).equalsIgnoreCase(ALLOW_MOCK);
    }

}
