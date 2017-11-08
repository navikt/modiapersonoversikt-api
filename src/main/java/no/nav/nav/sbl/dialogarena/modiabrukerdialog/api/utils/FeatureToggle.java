package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

public class FeatureToggle {
    static final String ENDRE_NAVN_PROPERTY_KEY = "visEndreNavnFunksjonalitet";
    private static final String DEFAULT_ENDRE_NAVN_FUNKSJONALITET = "false";

    public static boolean visEndreNavnFunksjonalitet() {
        return valueOf(getProperty(ENDRE_NAVN_PROPERTY_KEY, DEFAULT_ENDRE_NAVN_FUNKSJONALITET));
    }

    public static void enableEndreNavnFunksjonalitet() {
        setProperty(ENDRE_NAVN_PROPERTY_KEY, "true");
    }

    public static void disableEndreNavnFunksjonalitet() {
        setProperty(ENDRE_NAVN_PROPERTY_KEY, "false");
    }
}