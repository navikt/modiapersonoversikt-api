package no.nav.modiapersonoversikt.infrastructure.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService

class AktorIdPip(val pdl: PdlOppslagService) : Kabac.AttributeProvider<String> {
    companion object : Kabac.AttributeKey<String> {
        override val key = Key<String>(AktorIdPip::class.java.simpleName)
    }

    override val key = Companion.key
    override fun provide(ctx: EvaluationContext): String? {
        val fnr = ctx.requireValue(CommonAttributes.FNR)
        return pdl.hentAktorId(fnr)
    }
}
