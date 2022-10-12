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
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.RestConstants.ALLE_TEMA_HEADERVERDI
import no.nav.modiapersonoversikt.testutils.AuthContextRule
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
    private val machineToMachineTokenClient: BoundedMachineToMachineTokenClient = mockk()

    @Before
    fun before() {
        every { machineToMachineTokenClient.createMachineToMachineToken() } returns systemuserToken
    }

    @Test
    fun `riktige user-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifyUserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(machineToMachineTokenClient, client).hentIdenter("ident")
        }
    }

    @Test
    fun `riktige system-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifySystemuserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(machineToMachineTokenClient, client).hentAdressebeskyttelse("ident")
        }
    }

    private fun verifyUserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.NAV_CONSUMER_TOKEN_HEADER])
        assertEquals("Bearer ${userToken.serialize()}", request.headers[RestConstants.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    private fun verifySystemuserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.NAV_CONSUMER_TOKEN_HEADER])
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
