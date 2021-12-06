package no.nav.modiapersonoversikt.legacy.api.service.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.*

interface PdlOppslagService {
    fun sokPerson(kriterier: List<SokKriterier>): List<SokPerson.PersonSearchHit>
    fun hentPerson(fnr: String): HentPerson.Person?
    fun hentPersondata(fnr: String): HentPersondata.Person?
    fun hentTredjepartspersondata(fnrs: List<String>): List<HentTredjepartspersondata.HentPersonBolkResult>
    fun hentGeografiskTilknyttning(fnr: String): String?
    fun hentIdenter(fnr: String): HentIdenter.Identliste?
    fun hentAktorId(fnr: String): String?
    fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>?

    enum class SokKriterieRule {
        EQUALS,
        CONTAINS,
        AFTER,
        BEFORE
    }

    enum class PdlSokbareFelt(val feltnavn: String, val rule: SokKriterieRule) {
        NAVN("fritekst.navn", SokKriterieRule.CONTAINS),
        ADRESSE("fritekst.adresse", SokKriterieRule.CONTAINS),
        UTENLANDSK_ID("person.utenlandskIdentifikasjonsnummer.identifikasjonsnummer", SokKriterieRule.EQUALS),
        FODSELSDATO_FRA("person.foedsel.foedselsdato", SokKriterieRule.AFTER),
        FODSELSDATO_TIL("person.foedsel.foedselsdato", SokKriterieRule.BEFORE),
        KJONN("person.kjoenn.kjoenn", SokKriterieRule.EQUALS)
    }

    data class SokKriterier(val felt: PdlSokbareFelt, val value: String?) {
        fun asCriterion() =
            if (this.value == null) {
                null
            } else {
                SokPerson.Criterion(
                    fieldName = this.felt.feltnavn,
                    searchRule = when (this.felt.rule) {
                        SokKriterieRule.EQUALS -> SokPerson.SearchRule(equals = this.value)
                        SokKriterieRule.CONTAINS -> SokPerson.SearchRule(contains = this.value)
                        SokKriterieRule.AFTER -> SokPerson.SearchRule(after = this.value)
                        SokKriterieRule.BEFORE -> SokPerson.SearchRule(before = this.value)
                    }
                )
            }
    }
}
