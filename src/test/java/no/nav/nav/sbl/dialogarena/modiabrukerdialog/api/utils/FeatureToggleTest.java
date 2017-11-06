package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import org.junit.Test;

import static java.lang.System.setProperty;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.FeatureToggle.ENDRE_NAVN_PROPERTY_KEY;
import static org.junit.Assert.assertEquals;

public class FeatureToggleTest {

    @Test
    public void visDelviseSvarDefaulterTilFalse() {
        System.clearProperty(ENDRE_NAVN_PROPERTY_KEY);
        assertEquals(false, FeatureToggle.visEndreNavnFunksjonalitet());
    }

    @Test
    public void visDelviseSvarLeserProperty() {
        setProperty(ENDRE_NAVN_PROPERTY_KEY, "true");
        assertEquals(true, FeatureToggle.visEndreNavnFunksjonalitet());
    }
}