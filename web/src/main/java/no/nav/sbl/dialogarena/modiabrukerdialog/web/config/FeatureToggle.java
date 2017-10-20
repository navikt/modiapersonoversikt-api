package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;

public class FeatureToggle {
    static final String VIS_DELVISE_SVAR = "visDelviseSvarFunksjonalitet";
    private static final String DEFAULT_VIS_DELVISE_SVAR_FUNKSJONALITET = "false";

    public static boolean visDelviseSvarFunksjonalitet() {
        return valueOf(getProperty(VIS_DELVISE_SVAR, DEFAULT_VIS_DELVISE_SVAR_FUNKSJONALITET));
    }
}
