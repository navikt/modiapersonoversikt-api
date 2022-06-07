package no.nav.modiapersonoversikt.service.pdl

import no.nav.modiapersonoversikt.consumer.pdl.generated.*
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.SokKriterieRule.*

interface PdlOppslagService {
    fun sokPerson(kriterier: List<PdlKriterie>): List<SokPerson.PersonSearchHit>
    fun hentPersondata(fnr: String): HentPersondata.Person?
    fun hentTredjepartspersondata(fnrs: List<String>): List<HentTredjepartspersondata.HentPersonBolkResult>
    fun hentGeografiskTilknyttning(fnr: String): String?
    fun hentIdenter(fnr: String): HentIdenter.Identliste?
    fun hentAktorId(fnr: String): String?
    fun hentFnr(aktorid: String): String?

    fun hentAdressebeskyttelse(fnr: String): List<HentAdressebeskyttelse.Adressebeskyttelse>

    enum class SokKriterieRule {
        EQUALS,
        CONTAINS,
        FUZZY_MATCH,
        AFTER,
        BEFORE
    }

    enum class PdlSokeOmfang(val verdi: Boolean?) {
        HISTORISK_OG_GJELDENDE(null),
        HISTORISK(true),
        GJELDENDE(false)
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
        val boost: Float? = null,
        val searchHistorical: PdlSokeOmfang = PdlSokeOmfang.HISTORISK_OG_GJELDENDE
    ) {
        fun asCriterion() =
            if (value.isNullOrEmpty()) {
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
                    },
                    searchHistorical = searchHistorical.verdi
                )
            }
    }
}
