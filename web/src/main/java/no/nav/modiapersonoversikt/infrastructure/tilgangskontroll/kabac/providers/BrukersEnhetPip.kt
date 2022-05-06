package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class BrukersEnhetPip(private val norgApi: NorgApi) : Kabac.AttributeProvider<EnhetId> {
    override val key = Companion.key
    companion object : Kabac.AttributeKey<EnhetId> {
        override val key = Key<EnhetId>(BrukersEnhetPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): EnhetId? {
        return ctx.getValue(BrukersGeografiskeTilknyttningPip)
            ?.let {
                norgApi.finnNavKontor(it, null)
            }
            ?.let { EnhetId(it.enhetId) }
    }
}
