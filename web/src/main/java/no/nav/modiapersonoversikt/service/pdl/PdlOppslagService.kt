package no.nav.modiapersonoversikt.service.pdl

import no.nav.modiapersonoversikt.consumer.pdl.generated.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentidenter.Identliste
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.HentPersonBolkResult
import no.nav.modiapersonoversikt.consumer.pdl.generated.inputs.Criterion
import no.nav.modiapersonoversikt.consumer.pdl.generated.inputs.SearchRule
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.PersonSearchHit
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.SokKriterieRule.*

interface PdlOppslagService {
    fun sokPerson(kriterier: List<PdlKriterie>): List<PersonSearchHit>

    fun hentPersondata(fnr: String): HentPersondata.Result?

    fun hentTredjepartspersondata(fnrs: List<String>): List<HentPersonBolkResult>

    fun hentGeografiskTilknyttning(fnr: String): String?

    fun hentIdenter(fnr: String): Identliste?

    fun hentFolkeregisterIdenter(fnr: String): Identliste?

    fun hentAktorId(fnr: String): String?

    fun hentFnr(aktorid: String): String?

    enum class SokKriterieRule {
        EQUALS,
        CONTAINS,
        FUZZY_MATCH,
        AFTER,
        BEFORE,
    }

    enum class PdlSokeOmfang(val verdi: Boolean?) {
        HISTORISK_OG_GJELDENDE(null),
        HISTORISK(true),
        GJELDENDE(false),
    }

    enum class PdlFelt(val feltnavn: String, val rule: SokKriterieRule) {
        NAVN("fritekst.navn", FUZZY_MATCH),
        ADRESSE("fritekst.adresser", CONTAINS),
        UTENLANDSK_ID("person.utenlandskIdentifikasjonsnummer.identifikasjonsnummer", EQUALS),
        FODSELSDATO_FRA("person.foedselsdato.foedselsdato", AFTER),
        FODSELSDATO_TIL("person.foedselsdato.foedselsdato", BEFORE),
        KJONN("person.kjoenn.kjoenn", EQUALS),
    }

    data class PdlKriterie(
        val felt: PdlFelt,
        val value: String?,
        val boost: Double? = null,
        val searchHistorical: PdlSokeOmfang = PdlSokeOmfang.HISTORISK_OG_GJELDENDE,
    ) {
        fun asCriterion() =
            if (value.isNullOrEmpty()) {
                null
            } else {
                Criterion(
                    fieldName = felt.feltnavn,
                    searchRule =
                        when (felt.rule) {
                            EQUALS -> SearchRule(equals = this.value, boost = boost)
                            CONTAINS -> SearchRule(contains = this.value, boost = boost)
                            FUZZY_MATCH -> SearchRule(fuzzy = this.value, boost = boost)
                            AFTER -> SearchRule(after = this.value, boost = boost)
                            BEFORE -> SearchRule(before = this.value, boost = boost)
                        },
                    searchHistorical = searchHistorical.verdi,
                )
            }
    }
}
