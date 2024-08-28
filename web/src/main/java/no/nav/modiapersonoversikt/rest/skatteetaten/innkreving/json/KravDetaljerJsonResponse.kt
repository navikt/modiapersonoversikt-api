package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving.json

import kotlinx.datetime.LocalDate
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Krav
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Kravdetaljer
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Kravgrunnlag

data class KravDetaljerJsonResponse(
    val kravgrunnlag: KravgrunnlagJson,
    val krav: List<KravJson>,
) {
    companion object {
        fun fromDomain(kravdetaljer: Kravdetaljer): KravDetaljerJsonResponse =
            KravDetaljerJsonResponse(
                kravgrunnlag = KravgrunnlagJson.fromDomain(kravdetaljer.kravgrunnlag),
                krav = kravdetaljer.krav.map(KravJson::fromDomain),
            )
    }
}

data class KravgrunnlagJson(
    val datoNaarKravVarBesluttetHosOppdragsgiver: LocalDate?,
) {
    companion object {
        fun fromDomain(kravgrunnlag: Kravgrunnlag): KravgrunnlagJson =
            KravgrunnlagJson(
                datoNaarKravVarBesluttetHosOppdragsgiver = kravgrunnlag.datoNaarKravVarBesluttetHosOppdragsgiver,
            )
    }
}

data class KravJson(
    val kravType: String,
    val opprinneligBeløp: Double,
    val gjenståendeBeløp: Double?,
) {
    companion object {
        fun fromDomain(krav: Krav): KravJson =
            KravJson(
                kravType = krav.kravType,
                opprinneligBeløp = krav.opprinneligBeløp,
                gjenståendeBeløp = krav.gjenståendeBeløp,
            )
    }
}
