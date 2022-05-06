package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

class VeiledersRegionEnheterPip(private val norgApi: NorgApi) : Kabac.AttributeProvider<List<EnhetId>> {
    override val key = Companion.key
    companion object : Kabac.AttributeKey<List<EnhetId>> {
        override val key = Key<List<EnhetId>>(VeiledersRegionEnheterPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): List<EnhetId> {
        val enheter = ctx.requireValue(VeiledersEnheterPip)
        return norgApi.hentRegionalEnheter(enheter)
    }
}
