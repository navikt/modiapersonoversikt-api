package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling

import java.lang.System.getProperty
import java.lang.System.setProperty

fun visFeature(feature: Feature) = getProperty(feature.propertyKey, feature.defaultValue.toString())!!.toBoolean()

fun enableFeature(feature: Feature) { setProperty(feature.propertyKey, "true") }

fun disableFeature(feature: Feature) { setProperty(feature.propertyKey, "false") }
