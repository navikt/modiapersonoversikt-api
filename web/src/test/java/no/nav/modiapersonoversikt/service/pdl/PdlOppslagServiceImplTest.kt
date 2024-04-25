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
            PdlOppslagServiceImpl(machineToMachineTokenClient, oboTokenProvider, client).hentIdenter("ident")
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
                machineToMachineTokenClient,
                oboTokenProvider,
                client,
            ).hentTredjepartspersondata(listOf("ident"))
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

    private fun createMockGraphQLClient(handler: MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData): GraphQLKtorClient {
        return GraphQLKtorClient(
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
}
