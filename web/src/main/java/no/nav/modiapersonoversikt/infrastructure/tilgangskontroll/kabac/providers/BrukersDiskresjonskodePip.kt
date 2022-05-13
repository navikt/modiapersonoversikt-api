package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAdressebeskyttelse
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService

class BrukersDiskresjonskodePip(private val pdl: PdlOppslagService) : Kabac.PolicyInformationPoint<BrukersDiskresjonskodePip.Kode> {
    enum class Kode { KODE6, KODE7 }

    override val key = Companion.key
    companion object : Kabac.AttributeKey<Kode> {
        override val key = Key<Kode>(BrukersDiskresjonskodePip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Kode? {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        return pdl.hentAdressebeskyttelse(fnr.get()).finnStrengesteKode()
    }

    private fun List<HentAdressebeskyttelse.Adressebeskyttelse>.finnStrengesteKode(): Kode? {
        return this
            .mapNotNull {
                when (it.gradering) {
                    HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG, HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND -> Kode.KODE6
                    HentAdressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG -> Kode.KODE7
                    else -> null
                }
            }.minOrNull()
    }
}
