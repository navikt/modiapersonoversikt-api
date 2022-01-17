package no.nav.modiapersonoversikt.legacy.api.service.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.*
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService.SokKriterieRule.*

interface PdlOppslagService {
    fun sokPerson(kriterier: List<PdlKriterie>): List<SokPerson.PersonSearchHit>
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentPersondata(fnr: String): HentPersondata.Person?
    fun hentTredjepartspersondata(fnrs: List<String>): List<HentTredjepartspersondata.HentPersonBolkResult>
    fun hentGeografiskTilknyttning(fnr: String): String?
    fun hentIdenter(fnr: String): HentIdenter.Identliste?
    fun hentAktorId(fnr: String): String?
    fun hentFnr(aktorid: String): String?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?

    enum class SokKriterieRule {
        EQUALS,
        CONTAINS,
        FUZZY_MATCH,
        AFTER,
        BEFORE
    }

    enum class PdlFelt(val feltnavn: String, val rule: SokKriterieRule) {
        NAVN("fritekst.navn", FUZZY_MATCH),
        FORNAVN("person.navn.fornavn", FUZZY_MATCH),
        ETTERNAVN("person.navn.etternavn", FUZZY_MATCH),
        MELLOMNAVN("person.navn.mellomnavn", FUZZY_MATCH),
        ADRESSE("fritekst.adresser", CONTAINS),
        UTENLANDSK_ID("person.utenlandskIdentifikasjonsnummer.identifikasjonsnummer", EQUALS),
        FODSELSDATO_FRA("person.foedsel.foedselsdato", AFTER),
        FODSELSDATO_TIL("person.foedsel.foedselsdato", BEFORE),
        KJONN("person.kjoenn.kjoenn", EQUALS)
    }
    data class PdlKriterie(
        val felt: PdlFelt,
        val value: String?,
        val boost: Float? = null
    ) {
        fun asCriterion() =
            if (value == null) {
                null
            } else {
                SokPerson.Criterion(
                    fieldName = felt.feltnavn,
                    searchRule = when (felt.rule) {
                        EQUALS -> SokPerson.SearchRule(equals = this.value, boost = boost)
                        CONTAINS -> SokPerson.SearchRule(contains = this.value, boost = boost)
                        FUZZY_MATCH -> SokPerson.SearchRule(fuzzy = this.value, boost = boost)
                        AFTER -> SokPerson.SearchRule(after = this.value, boost = boost)
                        BEFORE -> SokPerson.SearchRule(before = this.value, boost = boost)
                    }
                )
            }
    }
}
