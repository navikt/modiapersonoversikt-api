package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.infrastructure.RestConstants.ALLE_TEMA_HEADERVERDI
import no.nav.modiapersonoversikt.testutils.AuthContextRule
import no.nav.modiapersonoversikt.utils.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URL

internal class PdlOppslagServiceImplTest {
    val userToken = PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())

    @Rule
    @JvmField
    val subject = AuthContextRule(
        AuthContext(
            UserRole.INTERN,
            userToken
        )
    )
    val systemuserToken = "RND-STS-TOKEN"
    val stsMock: SystemUserTokenProvider = mockk()

    @Before
    fun before() {
        every { stsMock.systemUserToken } returns systemuserToken
    }

    @Test
    fun `riktige user-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifyUserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(stsMock, client).hentIdenter("ident")
        }
    }

    @Test
    fun `riktige system-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifySystemuserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(stsMock, client).hentNavnBolk(listOf("ident"))
        }
    }

    fun verifyUserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.NAV_CONSUMER_TOKEN_HEADER])
        assertEquals("Bearer ${userToken.serialize()}", request.headers[RestConstants.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    fun verifySystemuserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.NAV_CONSUMER_TOKEN_HEADER])
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    fun createMockGraphQLClient(handler: MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData): GraphQLClient<*> {
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
