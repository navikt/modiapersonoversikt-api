package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling;

import org.junit.Test;

import static java.lang.System.setProperty;
import static org.junit.Assert.assertEquals;

public class FeatureToggleTest {

    @Test
    public void visDelviseSvarDefaulterTilFalse() {
        System.clearProperty(Feature.DELVISE_SVAR.propertyKey);
        assertEquals(false, FeatureToggle.visFeature(Feature.DELVISE_SVAR));
    }

    @Test
    public void visDelviseSvarLeserProperty() {
        setProperty(Feature.DELVISE_SVAR.propertyKey, "true");
        assertEquals(true, FeatureToggle.visFeature(Feature.DELVISE_SVAR));
    }
}