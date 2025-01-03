package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key
import org.slf4j.LoggerFactory

class BrukersEnhetPip(
    private val norgApi: NorgApi,
) : Kabac.PolicyInformationPoint<EnhetId?> {
    private val log = LoggerFactory.getLogger(BrukersEnhetPip::class.java)
    override val key = Companion.key

    companion object : Kabac.AttributeKey<EnhetId?> {
        override val key = Key<EnhetId?>(BrukersEnhetPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): EnhetId? {
        val gt = ctx.getValue(BrukersGeografiskeTilknyttningPip)
        return gt
            ?.runCatching {
                norgApi.finnNavKontor(this, null)
            }?.onFailure {
                log.warn("Kunne ikke hente ut brukers enhet", it)
            }?.getOrNull()
            ?.let { EnhetId(it.enhetId) }
    }
}
