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
import no.nav.modiapersonoversikt.infrastructure.RestConstants.*
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.http.LoggingGraphqlClient
import no.nav.modiapersonoversikt.infrastructure.http.assertNoErrors
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.*
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import java.net.URL

@KtorExperimentalAPI
@CacheConfig(cacheNames = ["pdlCache"], keyGenerator = "userkeygenerator")
open class PdlOppslagServiceImpl constructor(
    private val stsService: SystemUserTokenProvider,
    private val machineToMachineTokenClient: BoundedMachineToMachineTokenClient,
    private val oboTokenClient: BoundedOnBehalfOfTokenClient,
    private val pdlClient: GraphQLClient<*>
) : PdlOppslagService {
    constructor(
        stsService: SystemUserTokenProvider,
        machineToMachineTokenClient: BoundedMachineToMachineTokenClient,
        oboTokenClient: BoundedOnBehalfOfTokenClient
    ) : this(stsService, machineToMachineTokenClient, oboTokenClient, createClient())

    @Cacheable(unless = "#result == null")
    override fun hentPersondata(fnr: String): HentPersondata.Result? = runBlocking {
        HentPersondata(pdlClient)
            .execute(HentPersondata.Variables(fnr), userTokenAuthorizationHeaders)
            .data
    }

    @Cacheable(unless = "#result == null")
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

    @Cacheable(unless = "#result == null")
    override fun hentIdenter(fnr: String): HentIdenter.Identliste? = runBlocking {
        HentIdenter(pdlClient)
            .execute(HentIdenter.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
    }

    @Cacheable(unless = "#result == null")
    override fun hentGeografiskTilknyttning(fnr: String): String? = runBlocking {
        HentGeografiskTilknyttning(pdlClient)
            .execute(HentGeografiskTilknyttning.Variables(fnr), userTokenAuthorizationHeaders)
            .data
            ?.hentGeografiskTilknytning
            ?.run {
                gtBydel ?: gtKommune ?: gtLand
            }
    }

    @Cacheable(unless = "#result == null")
    override fun hentAktorId(fnr: String): String? = hentAktivIdent(fnr, IdentGruppe.AKTORID)

    @Cacheable(unless = "#result == null")
    override fun hentFnr(aktorid: String): String? = hentAktivIdent(aktorid, IdentGruppe.FOLKEREGISTERIDENT)

    @Cacheable(unless = "#result == null")
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

    @Cacheable(unless = "#result == null")
    override fun hentAdressebeskyttelse(fnr: String): List<HentAdressebeskyttelse.Adressebeskyttelse> = runBlocking {
        HentAdressebeskyttelse(pdlClient)
            .execute(HentAdressebeskyttelse.Variables(fnr), systemTokenAuthorizationHeaders)
            .data
            ?.hentPerson
            ?.adressebeskyttelse
            ?: emptyList()
    }

    private fun hentAktivIdent(ident: String, gruppe: IdentGruppe): String? = runBlocking {
        HentAktorid(pdlClient)
            .execute(HentAktorid.Variables(ident, listOf(gruppe)), userTokenAuthorizationHeaders)
            .data
            ?.hentIdenter
            ?.identer
            ?.firstOrNull()
            ?.ident
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        val azureToken = AuthContextUtils.requireToken()
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + oboTokenClient.exchangeOnBehalfOfToken(azureToken))
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    private val systemTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = machineToMachineTokenClient.createMachineToMachineToken()
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
    }

    companion object {
        private val pdlApiUrl: URL = EnvironmentUtils.getRequiredProperty("PDL_API_URL").let(::URL)

        fun createClient() = LoggingGraphqlClient("PDL", pdlApiUrl)
    }
}
