package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.pdl.generated.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAktorid.IdentGruppe
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.http.LoggingGraphqlClient
import no.nav.modiapersonoversikt.infrastructure.http.assertNoErrors
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants.*
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.*
import java.net.URL
import kotlin.collections.set

@KtorExperimentalAPI
open class PdlOppslagServiceImpl constructor(
    private val stsService: SystemUserTokenProvider,
    private val pdlClient: GraphQLClient<*>
) : PdlOppslagService {
    constructor(stsService: SystemUserTokenProvider) : this(stsService, createClient())

    override fun hentPerson(fnr: String): HentPerson.Person? = runBlocking {
        HentPerson(pdlClient)
            .execute(HentPerson.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentPerson
    }

    override fun hentPersondata(fnr: String): HentPersondata.Person? = runBlocking {
        HentPersondata(pdlClient)
            .execute(HentPersondata.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentPerson
    }

    override fun hentTredjepartspersondata(fnrs: List<String>): List<HentTredjepartspersondata.HentPersonBolkResult> = runBlocking {
        if (fnrs.isEmpty()) {
            emptyList()
        } else {
            HentTredjepartspersondata(pdlClient)
                .execute(HentTredjepartspersondata.Variables(fnrs), systemTokenAuthorizationHeaders)
                .data
                ?.hentPersonBolk
                ?: emptyList()
        }
    }

    override fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn?>? {
        if (fnrs.isEmpty()) {
            return emptyMap()
        }

        return runBlocking {
            HentNavnBolk(pdlClient).execute(HentNavnBolk.Variables(fnrs), systemTokenAuthorizationHeaders)
        }
            .data
            ?.hentPersonBolk
            ?.fold(mutableMapOf()) { acc, bolkResult ->
                acc[bolkResult.ident] = bolkResult.person?.navn?.get(0)
                acc
            }
    }

    override fun hentIdenter(fnr: String): HentIdenter.Identliste? = runBlocking {
        HentIdenter(pdlClient)
            .execute(HentIdenter.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
    }

    override fun hentGeografiskTilknyttning(fnr: String): String? = runBlocking {
        HentGeografiskTilknyttning(pdlClient)
            .execute(HentGeografiskTilknyttning.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentGeografiskTilknytning
            ?.run {
                gtBydel ?: gtKommune ?: gtLand
            }
    }

    override fun hentAktorId(fnr: String): String? = hentAktivIdent(fnr, IdentGruppe.AKTORID)
    override fun hentFnr(aktorid: String): String? = hentAktivIdent(aktorid, IdentGruppe.FOLKEREGISTERIDENT)

    override fun sokPerson(kriterier: List<PdlKriterie>): List<SokPerson.PersonSearchHit> = runBlocking {
        val paging = SokPerson.Paging(
            pageNumber = 1,
            resultsPerPage = 30
        )

        val criteria = kriterier.mapNotNull { it.asCriterion() }
        if (criteria.isEmpty()) {
            emptyList()
        } else {
            SokPerson(pdlClient)
                .execute(
                    SokPerson.Variables(paging, criteria),
                    userTokenAuthorizationHeaders
                )
                .assertNoErrors()
                .data
                ?.sokPerson
                ?.hits
                ?: emptyList()
        }
    }

    fun hentAktivIdent(ident: String, gruppe: IdentGruppe): String? = runBlocking {
        HentAktorid(pdlClient)
            .execute(HentAktorid.Variables(ident, listOf(gruppe)), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
            ?.identer
            ?.firstOrNull()
            ?.ident
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserToken
        val userToken: String = AuthContextUtils.requireToken()

        header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + userToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    private val systemTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserToken

        header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    companion object {
        private val pdlApiUrl: URL = EnvironmentUtils.getRequiredProperty("PDL_API_URL").let(::URL)

        fun createClient() = LoggingGraphqlClient("PDL", pdlApiUrl)
    }
}
