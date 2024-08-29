package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving.json

import kotlinx.datetime.LocalDate
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Grunnlag
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Innkrevingskrav
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Krav

data class InnkrevingskravJsonResponse(
    val kravgrunnlag: KravgrunnlagJson,
    val krav: List<KravJson>,
) {
    companion object {
        fun fromDomain(innkrevingskrav: Innkrevingskrav): InnkrevingskravJsonResponse =
            InnkrevingskravJsonResponse(
                kravgrunnlag = KravgrunnlagJson.fromDomain(innkrevingskrav.grunnlag),
                krav = innkrevingskrav.krav.map(KravJson::fromDomain),
            )
    }
}

data class KravgrunnlagJson(
    val datoNaarKravVarBesluttetHosOppdragsgiver: LocalDate?,
) {
    companion object {
        fun fromDomain(grunnlag: Grunnlag): KravgrunnlagJson =
            KravgrunnlagJson(
                datoNaarKravVarBesluttetHosOppdragsgiver = grunnlag.datoNaarKravVarBesluttetHosOppdragsgiver,
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
