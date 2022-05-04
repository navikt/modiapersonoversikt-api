package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.service.unleash.Feature

object CommonAttributes {
    private val base: String = CommonAttributes::class.java.simpleName

    val ENHET = Key<EnhetId>("$base.enhet")
    val FNR = Key<EnhetId>("$base.fnr")
    val AKTOR_ID = Key<EnhetId>("$base.aktor_id")
    val HENVENDELSE_KJEDE_ID = Key<String>("$base.henvendelse_kjede_id")
    val FEATURE_TOGGLE = Key<Feature>("$base.feature_toggle")
}
