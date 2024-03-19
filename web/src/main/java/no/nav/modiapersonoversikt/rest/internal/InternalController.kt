package no.nav.modiapersonoversikt.rest.internal

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.token_client.client.OnBehalfOfTokenClient
import no.nav.modiapersonoversikt.consumer.pdl.generated.SokPerson
import no.nav.modiapersonoversikt.consumer.pdl.generated.inputs.Criterion
import no.nav.modiapersonoversikt.consumer.pdl.generated.inputs.Paging
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
import no.nav.modiapersonoversikt.utils.exchangeOnBehalfOfToken
import no.nav.personoversikt.common.typeanalyzer.CaptureStats
import no.nav.personoversikt.common.typeanalyzer.Formatter
import no.nav.personoversikt.common.typeanalyzer.KotlinFormat
import no.nav.personoversikt.common.typeanalyzer.TypescriptFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/internal")
class InternalController
    @Autowired
    constructor(
        private val machineToMachineTokenClient: MachineToMachineTokenClient,
        private val onBehalfOfTokenClient: OnBehalfOfTokenClient,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        data class Tokens(val user: String, val obs: String?, val system: String?)

        private val pdlClient = PdlOppslagServiceImpl.createClient()

        @GetMapping("/tokens")
        fun hentSystembrukerToken(
            @RequestParam(value = "scope", required = false) scope: String?,
        ): Tokens {
            val token = AuthContextUtils.getToken().orElse("")
            return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .check(Policies.kanBrukeInternal)
                .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Tokens)) {
                    Tokens(
                        user = token,
                        system =
                            scope?.let {
                                machineToMachineTokenClient.createMachineToMachineToken(DownstreamApi.parse(it))
                            },
                        obs =
                            scope?.let {
                                onBehalfOfTokenClient.exchangeOnBehalfOfToken(DownstreamApi.parse(it), token)
                            },
                    )
                }
        }

        @PostMapping("/pdlsok")
        fun pdlPersonsok(
            @RequestBody criteria: List<Criterion>,
        ): GraphQLClientResponse<SokPerson.Result> {
            return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .check(Policies.kanBrukeInternal)
                .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Pdlsok)) {
                    runBlocking {
                        val paging = Paging(pageNumber = 1, resultsPerPage = 30)
                        pdlClient.execute(SokPerson(SokPerson.Variables(paging, criteria)), systemTokenAuthHeader)
                    }
                }
        }

        @GetMapping("/typeanalyzer")
        fun getTypeanalyzerStats(): Map<String, CaptureStats> {
            return Typeanalyzers.values().associate {
                it.name to it.analyzer.stats
            }
        }

        @GetMapping("/typeanalyzer/{name}", produces = [MediaType.TEXT_PLAIN_VALUE])
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
            header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + systemuserToken)
            header(RestConstants.TEMA_HEADER, RestConstants.ALLE_TEMA_HEADERVERDI)
        }
    }
