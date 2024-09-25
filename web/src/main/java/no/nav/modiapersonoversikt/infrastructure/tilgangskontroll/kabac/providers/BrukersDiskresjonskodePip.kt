package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.AdressebeskyttelseGradering
import no.nav.modiapersonoversikt.consumer.pdlPip.PdlPipApi
import no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.models.PipAdressebeskyttelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class BrukersDiskresjonskodePip(
    private val pdlPip: PdlPipApi,
) : Kabac.PolicyInformationPoint<BrukersDiskresjonskodePip.Kode?> {
    enum class Kode { KODE6, KODE7 }

    override val key = Companion.key

    companion object : Kabac.AttributeKey<Kode?> {
        override val key = Key<Kode?>(BrukersDiskresjonskodePip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Kode? {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        return pdlPip.hentAdresseBeskyttelse(fnr.get())?.finnStrengesteKode()
    }

    private fun List<PipAdressebeskyttelse>.finnStrengesteKode(): Kode? =
        this
            .mapNotNull {
                when (it.gradering) {
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG.toString(),
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND.toString(),
                    -> Kode.KODE6
                    AdressebeskyttelseGradering.FORTROLIG.toString() -> Kode.KODE7
                    else -> null
                }
            }.minOrNull()
}
