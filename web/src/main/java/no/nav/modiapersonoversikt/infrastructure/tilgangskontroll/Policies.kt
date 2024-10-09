package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.HenvendelseTilhorerBrukerPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.KanBrukeInternalPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilBrukerPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilInnkrevingskravPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilModiaPolicy
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies.TilgangTilTemaPolicy
import no.nav.personoversikt.common.kabac.AttributeValue
import no.nav.personoversikt.common.kabac.Kabac

object Policies {
    @JvmField
    val tilgangTilModia = TilgangTilModiaPolicy.withAttributes()

    @JvmStatic
    fun tilgangTilBruker(eksternBrukerId: EksternBrukerId) =
        TilgangTilBrukerPolicy.withAttributes(
            eksternBrukerId.toAttributeValue(),
        )

    @JvmStatic
    fun tilgangTilTema(
        enhet: EnhetId,
        tema: String,
    ) = TilgangTilTemaPolicy.withAttributes(
        CommonAttributes.ENHET.withValue(enhet),
        CommonAttributes.TEMA.withValue(tema),
    )

    @JvmStatic
    fun henvendelseTilhorerBruker(
        eksternBrukerId: EksternBrukerId,
        kjedeId: String,
    ) = HenvendelseTilhorerBrukerPolicy.withAttributes(
        eksternBrukerId.toAttributeValue(),
        CommonAttributes.HENVENDELSE_KJEDE_ID.withValue(kjedeId),
    )

    @JvmStatic
    fun tilgangTilInnkrevingskrav() = TilgangTilInnkrevingskravPolicy.withAttributes()

    val kanBrukeInternal = KanBrukeInternalPolicy.withAttributes()

    private fun Kabac.Policy.withAttributes(vararg attributes: AttributeValue<*>) = PolicyWithAttributes(this, attributes.toList())

    private fun EksternBrukerId.toAttributeValue() =
        when (this) {
            is Fnr -> CommonAttributes.FNR.withValue(this)
            is AktorId -> CommonAttributes.AKTOR_ID.withValue(this)
            else -> throw IllegalArgumentException("Unsupported EksternBrukerID")
        }
}
