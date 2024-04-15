package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.pdlPip.PdlPipApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext

class BrukersFnrPip(private val pdlPip: PdlPipApi) : Kabac.PolicyInformationPoint<Fnr> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Fnr> {
        override val key = CommonAttributes.FNR
    }

    override fun provide(ctx: EvaluationContext): Fnr {
        val aktorId = ctx.getValue(CommonAttributes.AKTOR_ID)
        val fnr =
            checkNotNull(pdlPip.hentFnr(aktorId)) {
                "Fant ikke fnr for $aktorId"
            }
        return Fnr(fnr)
    }
}
