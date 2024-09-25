package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.AktorId
import no.nav.modiapersonoversikt.consumer.pdlPip.PdlPipApi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext

class BrukersAktorIdPip(
    private val pdlPip: PdlPipApi,
) : Kabac.PolicyInformationPoint<AktorId> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<AktorId> {
        override val key = CommonAttributes.AKTOR_ID
    }

    override fun provide(ctx: EvaluationContext): AktorId {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        val aktorid =
            checkNotNull(pdlPip.hentAktorId(fnr)) {
                "Fant ikke aktor id for $fnr"
            }
        return AktorId(aktorid)
    }
}
