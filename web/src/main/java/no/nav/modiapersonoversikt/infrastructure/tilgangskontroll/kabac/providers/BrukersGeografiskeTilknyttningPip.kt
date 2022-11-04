package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class BrukersGeografiskeTilknyttningPip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<String?> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<String?> {
        override val key = Key<String?>(BrukersGeografiskeTilknyttningPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): String? {
        val fnr = ctx.getValue(CommonAttributes.FNR).get()
        return pdl.hentGeografiskTilknyttning(fnr)
    }
}
