package no.nav.modiapersonoversikt.rest.internal

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.client.axsys.AxsysClient
import no.nav.common.client.nom.NomClient
import no.nav.common.client.nom.VeilederNavn
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.SokPerson
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/internal")
class InternalController @Autowired constructor(
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val tilgangskontroll: Tilgangskontroll,
    private val axsysClient: AxsysClient,
    private val nomClient: NomClient
) {
    data class Tokens(val user: String, val system: String)
    private val pdlClient = PdlOppslagServiceImpl.createClient()

    @GetMapping("/tokens")
    fun hentSystembrukerToken(): Tokens {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Tokens)) {
                Tokens(
                    user = AuthContextUtils.getToken().orElse("null"),
                    system = systemUserTokenProvider.systemUserToken
                )
            }
    }

    @PostMapping("/pdlsok")
    fun pdlPersonsok(@RequestBody criteria: List<SokPerson.Criterion>): GraphQLResponse<SokPerson.Result> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Pdlsok)) {
                runBlocking {
                    val paging = SokPerson.Paging(pageNumber = 1, resultsPerPage = 30)
                    SokPerson(pdlClient).execute(SokPerson.Variables(paging, criteria), systemTokenAuthHeader)
                }
            }
    }

    @GetMapping("/axsys")
    fun hentAxsysClient(): List<NavIdent> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Axsys)) {
                axsysClient.hentAnsatte(EnhetId("2990"))
            }
    }

    @GetMapping("/nom")
    fun hentNomClient(): List<VeilederNavn> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Nom)) {
                nomClient.finnNavn(
                    tilgangskontroll.context().hentSaksbehandlereMedTilgangTilInternal().map {
                    NavIdent(it)
                    }
                )
            }
    }

    private val systemTokenAuthHeader: HeadersBuilder = {
        val systemuserToken: String = systemUserTokenProvider.systemUserToken

        header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
        header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
        header(RestConstants.TEMA_HEADER, RestConstants.ALLE_TEMA_HEADERVERDI)
    }
}
