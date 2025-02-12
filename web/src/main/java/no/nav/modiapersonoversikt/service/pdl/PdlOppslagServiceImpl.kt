package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.modiapersonoversikt.consumer.pdl.generated.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.IdentGruppe
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentidenter.Identliste
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.HentPersonBolkResult
import no.nav.modiapersonoversikt.consumer.pdl.generated.inputs.Paging
import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.PersonSearchHit
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants.*
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.http.assertNoErrors
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.*
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

@CacheConfig(cacheNames = ["pdlCache"], keyGenerator = "userkeygenerator")
open class PdlOppslagServiceImpl(
    private val pdlClient: GraphQLKtorClient,
    private val machineToMachineTokenClient: BoundedMachineToMachineTokenClient,
    private val oboTokenClient: BoundedOnBehalfOfTokenClient,
) : PdlOppslagService {
    @Cacheable(unless = "#result == null")
    override fun hentPersondata(fnr: String): HentPersondata.Result? =
        runBlocking {
            pdlClient
                .execute(HentPersondata(HentPersondata.Variables(fnr)), userTokenAuthorizationHeaders)
                .assertNoErrors()
                .data
        }

    @Cacheable(unless = "#result == null")
    override fun hentTredjepartspersondata(fnrs: List<String>): List<HentPersonBolkResult> =
        runBlocking {
            if (fnrs.isEmpty()) {
                emptyList()
            } else {
                pdlClient
                    .execute(
                        HentTredjepartspersondata(HentTredjepartspersondata.Variables(fnrs)),
                        systemTokenAuthorizationHeaders,
                    ).assertNoErrors()
                    .data
                    ?.hentPersonBolk
                    ?: emptyList()
            }
        }

    @Cacheable(unless = "#result == null")
    override fun hentIdenter(fnr: String): Identliste? =
        runBlocking {
            pdlClient
                .execute(HentIdenter(HentIdenter.Variables(fnr)), userTokenAuthorizationHeaders)
                .assertNoErrors()
                .data
                ?.hentIdenter
        }

    @Cacheable(unless = "#result == null")
    override fun hentFolkeregisterIdenter(fnr: String): Identliste? =
        runBlocking {
            pdlClient
                .execute(
                    HentIdenter(HentIdenter.Variables(fnr, listOf(IdentGruppe.FOLKEREGISTERIDENT))),
                    userTokenAuthorizationHeaders,
                ).assertNoErrors()
                .data
                ?.hentIdenter
        }

    @Cacheable(unless = "#result == null")
    override fun hentGeografiskTilknyttning(fnr: String): String? =
        runBlocking {
            pdlClient
                .execute(
                    HentGeografiskTilknyttning(HentGeografiskTilknyttning.Variables(fnr)),
                    userTokenAuthorizationHeaders,
                ).assertNoErrors()
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
    override fun sokPerson(kriterier: List<PdlKriterie>): List<PersonSearchHit> =
        runBlocking {
            val paging =
                Paging(
                    pageNumber = 1,
                    resultsPerPage = 30,
                )

            val criteria = kriterier.mapNotNull { it.asCriterion() }
            if (criteria.isEmpty()) {
                emptyList()
            } else {
                pdlClient
                    .execute(
                        SokPerson(
                            SokPerson.Variables(paging, criteria),
                        ),
                        userTokenAuthorizationHeaders,
                    ).assertNoErrors()
                    .data
                    ?.sokPerson
                    ?.hits
                    ?: emptyList()
            }
        }

    private fun hentAktivIdent(
        ident: String,
        gruppe: IdentGruppe,
    ): String? =
        runBlocking {
            pdlClient
                .execute(HentAktorid(HentAktorid.Variables(ident, listOf(gruppe))), userTokenAuthorizationHeaders)
                .assertNoErrors()
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
        header(BEHANDLINGSNUMMER_HEADER, BEHANDLINGSNUMMER_HEADERVERDI)
    }

    private val systemTokenAuthorizationHeaders: HeadersBuilder = {
        val systemuserToken: String = machineToMachineTokenClient.createMachineToMachineToken()
        header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + systemuserToken)
        header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
        header(BEHANDLINGSNUMMER_HEADER, BEHANDLINGSNUMMER_HEADERVERDI)
    }
}
