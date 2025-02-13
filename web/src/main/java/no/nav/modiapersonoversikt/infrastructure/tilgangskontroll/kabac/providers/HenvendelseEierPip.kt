package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class HenvendelseEierPip(
    private val henvendelseService: SfHenvendelseService,
) : Kabac.PolicyInformationPoint<Fnr> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Fnr> {
        override val key = Key<Fnr>(HenvendelseEierPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Fnr {
        val kjedeId = ctx.getValue(CommonAttributes.HENVENDELSE_KJEDE_ID)
        val henvendelse: HenvendelseDTO = henvendelseService.hentHenvendelse(kjedeId)
        return Fnr(henvendelse.fnr)
    }
}
