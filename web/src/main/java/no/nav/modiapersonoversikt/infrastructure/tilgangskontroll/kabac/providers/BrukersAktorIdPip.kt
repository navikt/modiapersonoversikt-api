package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.AktorId
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext

class BrukersAktorIdPip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<AktorId> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<AktorId> {
        override val key = CommonAttributes.AKTOR_ID
    }

    override fun provide(ctx: EvaluationContext): AktorId {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        val aktorid = checkNotNull(pdl.hentAktorId(fnr.get())) {
            "Fant ikke aktor id for $fnr"
        }
        return AktorId(aktorid)
    }
}
