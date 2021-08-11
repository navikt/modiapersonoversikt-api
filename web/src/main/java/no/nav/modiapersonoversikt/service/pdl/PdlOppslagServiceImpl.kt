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
import no.nav.modiapersonoversikt.utils.KotlinUtils.filterValuesNotNull
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

    override fun hentNavnBolk(fnrs: List<String>): Map<String, HentNavnBolk.Navn> {
        if (fnrs.isEmpty()) {
            return emptyMap()
        }

        return runBlocking {
            HentNavnBolk(pdlClient).execute(HentNavnBolk.Variables(fnrs), systemTokenAuthorizationHeaders)
        }
            .data
            ?.hentPersonBolk
            ?.associateBy { it.ident }
            ?.mapValues { it.value.person?.navn?.first() }
            ?.filterValuesNotNull()
            ?: emptyMap()
    }

    override fun hentIdent(fnr: String): HentIdent.Identliste? = runBlocking {
        HentIdent(pdlClient)
            .execute(HentIdent.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
    }

    override fun sokPersonUtenlandskID(utenlandskID: String): List<SokPersonUtenlandskID.PersonSearchHit> = runBlocking {
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
            .execute(SokPersonUtenlandskID.Variables(paging = utenlandskIDPaging, criteria = listOf(utenlandskIDKriterie)), userTokenAuthorizationHeaders)
            .assertNoErrors()
            .data
            ?.sokPerson
            ?.hits
            ?: emptyList()
    }

    override fun hentGeografiskTilknyttning(fnr: String): HentGt.GeografiskTilknytning? = runBlocking {
        HentGt(pdlClient)
            .execute(HentGt.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentGeografiskTilknytning
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = stsService.systemUserToken
        val userToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }

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

        fun createClient() = LoggingGraphqlClient("PDL", pdlApiUrl) { variables ->
            when (variables) {
                null -> emptyList<String>() to variables
                is HentPerson.Variables -> {
                    val ident = variables.ident.let(PdlSyntetiskMapper::mapFnrTilPdl)
                    HentPerson.Variables(ident)
                }
                is HentNavnBolk.Variables -> {
                    val identer = variables.identer.map(PdlSyntetiskMapper::mapFnrTilPdl)
                    HentNavnBolk.Variables(identer)
                }
                is HentIdent.Variables -> {
                    val ident = variables.ident.let(PdlSyntetiskMapper::mapFnrTilPdl)
                    HentIdent.Variables(ident)
                }
                is SokPersonUtenlandskID.Variables -> variables
                else -> throw IllegalStateException("Unrecognized graphql variables type: ${variables.javaClass.simpleName}")
            }
        }
    }
}
