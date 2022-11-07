package no.nav.modiapersonoversikt.rest.internal

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.consumer.pdl.generated.SokPerson
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.http.HeadersBuilder
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.Typeanalyzers
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceConfig
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagServiceImpl
import no.nav.modiapersonoversikt.utils.DownstreamApi
import no.nav.modiapersonoversikt.utils.createMachineToMachineToken
import no.nav.personoversikt.common.typeanalyzer.Formatter
import no.nav.personoversikt.common.typeanalyzer.KotlinFormat
import no.nav.personoversikt.common.typeanalyzer.TypescriptFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/internal")
class InternalController @Autowired constructor(
    private val machineToMachineTokenClient: MachineToMachineTokenClient,
    private val tilgangskontroll: Tilgangskontroll

) {
    data class Tokens(val user: String, val system: String?)
    private val pdlClient = PdlOppslagServiceImpl.createClient()

    @GetMapping("/tokens")
    fun hentSystembrukerToken(@RequestParam(value = "scope", required = false) scope: String?): Tokens {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Tokens)) {
                Tokens(
                    user = AuthContextUtils.getToken().orElse("null"),
                    system = scope?.let {
                        machineToMachineTokenClient.createMachineToMachineToken(DownstreamApi.parse(it))
                    }
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

    @GetMapping("/types/{name}")
    fun getTypedefinition(
        @PathVariable("name") name: String,
        @RequestParam(value = "format", required = false) formatName: String?,
    ): String {
        val report = Typeanalyzers.valueOf(name).analyzer.report()
        val format = if (formatName == "typescript") TypescriptFormat else KotlinFormat
        return Formatter(format).print(report)
    }

    private val systemTokenAuthHeader: HeadersBuilder = {
        val systemuserToken: String = machineToMachineTokenClient.createMachineToMachineToken(PdlOppslagServiceConfig.downstreamApi)

        header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
        header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
        header(RestConstants.TEMA_HEADER, RestConstants.ALLE_TEMA_HEADERVERDI)
    }
}
