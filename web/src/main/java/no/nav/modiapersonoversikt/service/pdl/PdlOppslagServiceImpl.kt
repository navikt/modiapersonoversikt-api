package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.http.LoggingGraphqlClient
import no.nav.modiapersonoversikt.infrastructure.http.assertNoErrors
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.*
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants.*
import java.net.URL
import kotlin.collections.set

@KtorExperimentalAPI
class PdlOppslagServiceImpl constructor(
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
        HentTredjepartspersondata(pdlClient)
            .execute(HentTredjepartspersondata.Variables(fnrs), systemTokenAuthorizationHeaders)
            .data
            ?.hentPersonBolk
            ?: emptyList()
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

    override fun hentAktorId(fnr: String): String? = runBlocking {
        HentAktorid(pdlClient)
            .execute(HentAktorid.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
            ?.identer
            ?.firstOrNull()
            ?.ident
    }

    override fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.PersonSearchHit> =
        runBlocking {
            val utenlandskIDPaging = SokPersonUtenlandskID.Paging(
                pageNumber = 1,
                resultsPerPage = 30
            )
            val utenlandskIDKriterie = SokPersonUtenlandskID.Criterion(
                fieldName = "person.utenlandskIdentifikasjonsnummer.identifikasjonsnummer",
                searchRule = SokPersonUtenlandskID.SearchRule(
                    equals = utenlandskID
                )
            )
            SokPersonUtenlandskID(pdlClient)
                .execute(
                    SokPersonUtenlandskID.Variables(
                        paging = utenlandskIDPaging,
                        criteria = listOf(utenlandskIDKriterie)
                    ),
                    userTokenAuthorizationHeaders
                )
                .assertNoErrors()
                .data
                ?.sokPerson
                ?.hits
                ?: emptyList()
        }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserToken
        val userToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
            .orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }

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
