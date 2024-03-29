package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.personoversikt.common.kabac.utils.Key

object CommonAttributes {
    private val base: String = CommonAttributes::class.java.simpleName

    val ENHET = Key<EnhetId>("$base.enhet")
    val FNR = Key<Fnr>("$base.fnr")
    val AKTOR_ID = Key<AktorId>("$base.aktor_id")
    val HENVENDELSE_KJEDE_ID = Key<String>("$base.henvendelse_kjede_id")
    val TEMA = Key<String>("$base.tema")
}
