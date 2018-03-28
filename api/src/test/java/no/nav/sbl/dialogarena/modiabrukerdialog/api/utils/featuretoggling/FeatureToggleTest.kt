package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FeatureToggleTest {

    @Test
    fun visDelviseSvarDefaulterTilFalse() {
        System.clearProperty(Feature.DELVISE_SVAR.propertyKey)
        assertEquals(false, visFeature(Feature.DELVISE_SVAR))
    }

    @Test
    fun visDelviseSvarLeserProperty() {
        System.setProperty(Feature.DELVISE_SVAR.propertyKey, "true")
        assertEquals(true, visFeature(Feature.DELVISE_SVAR))
    }

}