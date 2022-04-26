package no.nav.modiapersonoversikt.rest.internal

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.consumer.pdl.generated.DebugPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.SokPerson
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/internal")
class InternalController @Autowired constructor(
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val tilgangskontroll: Tilgangskontroll

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

    @GetMapping("/debug-person/{fnr}")
    fun pdlDebugPerson(@PathVariable("fnr") fnr: String): GraphQLResponse<DebugPersondata.Result> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Personalia, AuditIdentifier.FNR to fnr)) {
                runBlocking {
                    DebugPersondata(pdlClient).execute(DebugPersondata.Variables(fnr))
                }
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

    private val systemTokenAuthHeader: HeadersBuilder = {
        val systemuserToken: String = systemUserTokenProvider.systemUserToken

        header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
        header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
        header(RestConstants.TEMA_HEADER, RestConstants.ALLE_TEMA_HEADERVERDI)
    }
}
