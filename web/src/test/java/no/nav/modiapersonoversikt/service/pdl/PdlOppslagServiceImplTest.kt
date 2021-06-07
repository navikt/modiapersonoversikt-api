package no.nav.modiapersonoversikt.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants
import no.nav.modiapersonoversikt.legacy.api.utils.RestConstants.ALLE_TEMA_HEADERVERDI
import no.nav.modiapersonoversikt.utils.SubjectRule
import no.nav.modiapersonoversikt.utils.TestUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URL

internal class PdlOppslagServiceImplTest {
    val userToken = "RND-USER-TOKEN"

    @Rule
    @JvmField
    val subject = SubjectRule(Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken(userToken, emptyMap<String, Any>())))
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
            PdlOppslagServiceImpl(stsMock, client).hentIdent("ident")
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
        assertEquals("Bearer $userToken", request.headers[RestConstants.AUTHORIZATION])
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
