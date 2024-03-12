package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext

class BrukersFnrPip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<Fnr> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Fnr> {
        override val key = CommonAttributes.FNR
    }

    override fun provide(ctx: EvaluationContext): Fnr {
        val aktorId = ctx.getValue(CommonAttributes.AKTOR_ID)
        val fnr =
            checkNotNull(pdl.hentFnr(aktorId.get())) {
                "Fant ikke fnr for $aktorId"
            }
        return Fnr(fnr)
    }
}
