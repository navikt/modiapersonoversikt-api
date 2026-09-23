package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.serialization.GraphQLClientKotlinxSerializer
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.RestConstants.ALLE_TEMA_HEADERVERDI
import no.nav.modiapersonoversikt.testutils.AuthContextRule
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat
import java.net.URL

internal class PdlOppslagServiceImplTest {
    private val userToken = PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())

    @Rule
    @JvmField
    val subject =
        AuthContextRule(
            AuthContext(
                UserRole.INTERN,
                userToken,
            ),
        )
    private val systemuserToken = "RND-STS-TOKEN"
    private val oboTokenProvider: BoundedOnBehalfOfTokenClient = mockk()
    private val machineToMachineTokenClient: BoundedMachineToMachineTokenClient = mockk()

    @Before
    fun before() {
        every { oboTokenProvider.exchangeOnBehalfOfToken(any()) } returns userToken.serialize()
        every { machineToMachineTokenClient.createMachineToMachineToken() } returns systemuserToken
    }

    @Test
    fun `riktige user-headere skal settes på requesten`() {
        val client =
            createMockGraphQLClient { request ->
                verifyUserTokenHeaders(request)
                respond("{}", HttpStatusCode.OK)
            }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(client, machineToMachineTokenClient, oboTokenProvider).hentIdenter("ident")
        }
    }

    @Test
    fun `riktige system-headere skal settes på requesten`() {
        val client =
            createMockGraphQLClient { request ->
                verifySystemuserTokenHeaders(request)
                respond("{}", HttpStatusCode.OK)
            }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(
                client,
                machineToMachineTokenClient,
                oboTokenProvider,
            ).hentTredjepartspersondata(listOf("ident"))
        }
    }

    @Test
    fun `sender pageNumber og resultsPerPage som paging til pdl`() {
        var capturedBody: String? = null
        val client =
            createMockGraphQLClient { request ->
                capturedBody = (request.body as io.ktor.http.content.TextContent).text
                respond(
                    """{"data":{"sokPerson":{"hits":[],"pageNumber":2,"totalHits":120,"totalPages":3}}}""",
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(client, machineToMachineTokenClient, oboTokenProvider)
                .sokPerson(
                    listOf(PdlOppslagService.PdlKriterie(PdlOppslagService.PdlFelt.FODSELSDATO_FRA, "1990-01-01")),
                    pageNumber = 2,
                    resultsPerPage = 50,
                )
        }

        assertNotNull(capturedBody)
        assertThat(capturedBody).contains("\"pageNumber\":2")
        assertThat(capturedBody).contains("\"resultsPerPage\":50")
    }

    @Test
    fun `mapper totalHits, totalPages og pageNumber fra pdl-respons`() {
        val client =
            createMockGraphQLClient {
                respond(
                    """{"data":{"sokPerson":{"hits":[],"pageNumber":2,"totalHits":120,"totalPages":3}}}""",
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        var resultat: PdlOppslagService.PdlSokResultat? = null
        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            resultat =
                PdlOppslagServiceImpl(client, machineToMachineTokenClient, oboTokenProvider)
                    .sokPerson(
                        listOf(PdlOppslagService.PdlKriterie(PdlOppslagService.PdlFelt.FODSELSDATO_FRA, "1990-01-01")),
                        pageNumber = 2,
                        resultsPerPage = 50,
                    )
        }

        assertEquals(2, resultat?.pageNumber)
        assertEquals(120, resultat?.totalHits)
        assertEquals(3, resultat?.totalPages)
    }

    @Test
    fun `resultsPerPage begrenses til pdl sitt maks paa 100`() {
        var capturedBody: String? = null
        val client =
            createMockGraphQLClient { request ->
                capturedBody = (request.body as io.ktor.http.content.TextContent).text
                respond(
                    """{"data":{"sokPerson":{"hits":[],"pageNumber":1,"totalHits":0,"totalPages":0}}}""",
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(client, machineToMachineTokenClient, oboTokenProvider)
                .sokPerson(
                    listOf(PdlOppslagService.PdlKriterie(PdlOppslagService.PdlFelt.FODSELSDATO_FRA, "1990-01-01")),
                    pageNumber = 1,
                    resultsPerPage = 500,
                )
        }

        assertNotNull(capturedBody)
        assertThat(capturedBody).contains("\"resultsPerPage\":100")
    }

    private fun verifyUserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer ${userToken.serialize()}", request.headers[RestConstants.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    private fun verifySystemuserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    private fun createMockGraphQLClient(handler: MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData): GraphQLKtorClient =
        GraphQLKtorClient(
            url = URL("http://dummy.no"),
            httpClient =
                HttpClient(engineFactory = MockEngine) {
                    engine {
                        addHandler { handler.invoke(this, it) }
                    }
                },
            serializer = GraphQLClientKotlinxSerializer(),
        )
}
