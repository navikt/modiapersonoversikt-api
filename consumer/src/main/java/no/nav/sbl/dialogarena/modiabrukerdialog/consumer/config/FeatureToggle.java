package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

public class FeatureToggle {
    static final String VIS_DELVISE_SVAR_PROPERTY_KEY = "visDelviseSvarFunksjonalitet";
    private static final String DEFAULT_VIS_DELVISE_SVAR_FUNKSJONALITET = "false";

    public static boolean visDelviseSvarFunksjonalitet() {
        return valueOf(getProperty(VIS_DELVISE_SVAR_PROPERTY_KEY, DEFAULT_VIS_DELVISE_SVAR_FUNKSJONALITET));
    }

    public static void enableDelviseSvarFunksjonalitet() {
        setProperty(VIS_DELVISE_SVAR_PROPERTY_KEY, "true");
    }

    public static void disableDelviseSvarFunksjonalitet() {
        setProperty(VIS_DELVISE_SVAR_PROPERTY_KEY, "false");
    }
}
