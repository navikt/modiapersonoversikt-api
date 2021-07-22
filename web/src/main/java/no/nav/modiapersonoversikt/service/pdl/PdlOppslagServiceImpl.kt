package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
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

    override fun hentPerson(ident: String): HentPerson.Person? = runBlocking {
        HentPerson(pdlClient)
            .execute(HentPerson.Variables(ident), userTokenAuthorizationHeaders)
            .data
            ?.hentPerson
    }

    override fun hentNavnBolk(identer: List<String>): Map<String, HentNavnBolk.Navn?>? {
        if (identer.isEmpty()) {
            return emptyMap()
        }

        return runBlocking {
            HentNavnBolk(pdlClient).execute(HentNavnBolk.Variables(identer), systemTokenAuthorizationHeaders)
        }
            .data
            ?.hentPersonBolk
            ?.fold(mutableMapOf()) { acc, bolkResult ->
                acc[bolkResult.ident] = bolkResult.person?.navn?.get(0)
                acc
            }
    }

    override fun hentIdenter(ident: String): HentIdenter.Identliste? = runBlocking {
        HentIdenter(pdlClient)
            .execute(HentIdenter.Variables(ident), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
    }

    override fun hentAktorId(ident: String): String? = runBlocking {
        HentAktorid(pdlClient)
            .execute(HentAktorid.Variables(ident), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
            ?.identer
            ?.firstOrNull()
            ?.ident
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

        fun createClient() = PdlClient(pdlApiUrl) { variables ->
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
                is HentIdenter.Variables -> {
                    val ident = variables.ident.let(PdlSyntetiskMapper::mapFnrTilPdl)
                    HentIdenter.Variables(ident)
                }
                is HentAktorid.Variables -> {
                    val ident = variables.ident.let(PdlSyntetiskMapper::mapFnrTilPdl)
                    HentAktorid.Variables(ident)
                } //
                is SokPersonUtenlandskID.Variables -> variables
                else -> throw IllegalStateException("Unrecognized graphql variables type: ${variables.javaClass.simpleName}")
            }
        }
    }
}
