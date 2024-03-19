package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.AdressebeskyttelseGradering
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentadressebeskyttelse.Adressebeskyttelse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class BrukersDiskresjonskodePip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<BrukersDiskresjonskodePip.Kode?> {
    enum class Kode { KODE6, KODE7 }

    override val key = Companion.key

    companion object : Kabac.AttributeKey<Kode?> {
        override val key = Key<Kode?>(BrukersDiskresjonskodePip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Kode? {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        return pdl.hentAdressebeskyttelse(fnr.get()).finnStrengesteKode()
    }

    private fun List<Adressebeskyttelse>.finnStrengesteKode(): Kode? {
        return this
            .mapNotNull {
                when (it.gradering) {
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG, AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND -> Kode.KODE6
                    AdressebeskyttelseGradering.FORTROLIG -> Kode.KODE7
                    else -> null
                }
            }.minOrNull()
    }
}
