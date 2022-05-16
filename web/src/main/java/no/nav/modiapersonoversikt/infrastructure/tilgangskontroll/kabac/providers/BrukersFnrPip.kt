package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService

class BrukersFnrPip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<Fnr> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Fnr> {
        override val key = CommonAttributes.FNR
    }

    override fun provide(ctx: EvaluationContext): Fnr? {
        val aktorId = ctx.getValue(CommonAttributes.AKTOR_ID)
        return pdl.hentFnr(aktorId.get())?.let(::Fnr)
    }
}
