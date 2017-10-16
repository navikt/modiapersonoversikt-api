package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.System.setProperty;
import static org.junit.Assert.*;

class FeatureToggleTest {

    @Test
    @DisplayName("Hvis property ikke er satt, skal delvise svar funksjonalitet ikke vises")
    void visDelviseSvarDefaulterTilFalse() {
        assertEquals(false, FeatureToggle.visDelviseSvarFunksjonalitet());
    }

    @Test
    @DisplayName("Delvise svar skal kun vises hvis det er angitt i properties")
    void visDelviseSvarLeserProperty() {
        setProperty(FeatureToggle.VIS_DELVISE_SVAR, "true");
        assertEquals(true, FeatureToggle.visDelviseSvarFunksjonalitet());
    }
}