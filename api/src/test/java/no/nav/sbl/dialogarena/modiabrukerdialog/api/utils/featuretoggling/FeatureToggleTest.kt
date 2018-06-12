package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FeatureToggleTest {

    @Test
    fun visFeatureDefaulterTilDefaultverdi() {
        System.clearProperty(Feature.SAMPLE_FEATURE.propertyKey)
        assertEquals(Feature.SAMPLE_FEATURE.defaultValue, visFeature(Feature.SAMPLE_FEATURE))
    }

    @Test
    fun visFeatureLeserProperty() {
        System.setProperty(Feature.SAMPLE_FEATURE.propertyKey, "true")
        assertEquals(true, visFeature(Feature.SAMPLE_FEATURE))
    }

    @Test
    fun enableFeatureSetterTilTrue(){
        enableFeature(Feature.SAMPLE_FEATURE)

        assertEquals("true", System.getProperty(Feature.SAMPLE_FEATURE.propertyKey))
    }


    @Test
    fun disableFeatureSetterTilFalse(){
        disableFeature(Feature.SAMPLE_FEATURE)

        assertEquals("false", System.getProperty(Feature.SAMPLE_FEATURE.propertyKey))
    }
}