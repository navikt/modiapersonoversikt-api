package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.AktorId
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService

class BrukersAktorIdPip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<AktorId> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<AktorId> {
        override val key = CommonAttributes.AKTOR_ID
    }

    override fun provide(ctx: EvaluationContext): AktorId? {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        return pdl.hentAktorId(fnr.get())?.let(::AktorId)
    }
}
