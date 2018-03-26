package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

public class FeatureToggle {

    public static boolean visFeature(Feature feature) {
        return valueOf(System.getProperty(feature.propertyKey, String.valueOf(feature.defaultValue)));
    }

    public static void toggleFeature(Feature feature) {
        setProperty(feature.propertyKey, "true");
    }

    public static void disableFeature(Feature feature) {
        setProperty(feature.propertyKey, "false");
    }

}