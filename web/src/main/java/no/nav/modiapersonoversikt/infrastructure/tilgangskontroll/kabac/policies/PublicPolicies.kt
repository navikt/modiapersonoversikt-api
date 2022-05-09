package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.AttributeValue
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.PolicyWithAttributes

object PublicPolicies {
    fun tilgangTilModia() = TilgangTilModiaPolicy.withAttributes()
    fun tilgangTilBruker(eksternBrukerId: EksternBrukerId) = TilgangTilBrukerPolicy.withAttributes(
        eksternBrukerId.toAttributeValue()
    )

    fun tilgangTilTema(enhet: EnhetId, tema: String) = TilgangTilTemaPolicy.withAttributes(
        CommonAttributes.ENHET.withValue(enhet),
        CommonAttributes.TEMA.withValue(tema)
    )

    fun henvendelseTilhorerBruker(eksternBrukerId: EksternBrukerId, kjedeId: String) = HenvendelseTilhorerBrukerPolicy.withAttributes(
        eksternBrukerId.toAttributeValue(),
        CommonAttributes.HENVENDELSE_KJEDE_ID.withValue(kjedeId)
    )
    fun kanBrukerInternal() = KanBrukeInternalPolicy.withAttributes()

    private fun Kabac.Policy.withAttributes(vararg attributes: AttributeValue<*>) = PolicyWithAttributes(this, attributes.toList())
    private fun EksternBrukerId.toAttributeValue() = when (this) {
        is Fnr -> CommonAttributes.FNR.withValue(this)
        is AktorId -> CommonAttributes.AKTOR_ID.withValue(this)
        else -> throw IllegalArgumentException("Unsupported EksternBrukerID")
    }
}
