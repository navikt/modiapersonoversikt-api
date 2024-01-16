package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.RestConstants.ALLE_TEMA_HEADERVERDI
import no.nav.modiapersonoversikt.infrastructure.http.GraphQLException
import no.nav.modiapersonoversikt.testutils.AuthContextRule
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL

@OptIn(KtorExperimentalAPI::class)
internal class PdlOppslagServiceImplTest {
    private val userToken = PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())

    @Rule
    @JvmField
    val subject = AuthContextRule(
        AuthContext(
            UserRole.INTERN,
            userToken
        )
    )
    private val systemuserToken = "RND-STS-TOKEN"
    private val stsClient: SystemUserTokenProvider = mockk()
    private val machineToMachineTokenClient: BoundedMachineToMachineTokenClient = mockk()
    private val oboTokenProvider: BoundedOnBehalfOfTokenClient = mockk()

    @Before
    fun before() {
        every { stsClient.systemUserToken } returns systemuserToken
        every { machineToMachineTokenClient.createMachineToMachineToken() } returns systemuserToken
        every { oboTokenProvider.exchangeOnBehalfOfToken(any()) } returns userToken.serialize()
    }

    @Test
    fun `riktige user-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifyUserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(stsClient, machineToMachineTokenClient, oboTokenProvider, client).hentIdenter("ident")
        }
    }

    @Test
    fun `riktige system-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifySystemuserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(
                stsClient,
                machineToMachineTokenClient,
                oboTokenProvider,
                client
            ).hentAdressebeskyttelse("ident")
        }
    }

    @Test
    fun `skal kaste feil om det kommer valideringsfeil på adressebeskyttelse-request`() {
        val client = createMockGraphQLClient { request ->
            verifySystemuserTokenHeaders(request)
            respond(
                "{\n" +
                    "  \"errors\": [\n" +
                    "    {\n" +
                    "      \"message\": \"Variable 'ident' has an invalid value: Variable 'ident' has coerced Null value for NonNull type 'ID!'\",\n" +
                    "      \"locations\": [\n" +
                    "        {\n" +
                    "          \"line\": 1,\n" +
                    "          \"column\": 8\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"extensions\": {\n" +
                    "        \"classification\": \"ValidationError\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"data\": null\n" +
                    "}",
                HttpStatusCode.OK
            )
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            assertThrows<GraphQLException> {
                PdlOppslagServiceImpl(
                    stsClient,
                    machineToMachineTokenClient,
                    oboTokenProvider,
                    client
                ).hentAdressebeskyttelse("ident")
            }
        }
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

    private fun createMockGraphQLClient(handler: MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData): GraphQLClient<*> {
        return GraphQLClient(
            url = URL("http://dummy.no"),
            engineFactory = MockEngine,
            configuration = {
                engine {
                    addHandler { handler.invoke(this, it) }
                }
            }
        )
    }
}
