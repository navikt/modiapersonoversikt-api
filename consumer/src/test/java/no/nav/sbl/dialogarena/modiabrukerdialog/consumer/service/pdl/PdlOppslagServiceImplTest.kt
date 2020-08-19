package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl

import com.expediagroup.graphql.client.GraphQLClient
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.ALLE_TEMA_HEADERVERDI
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.SubjectRule
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TestUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URL
import javax.ws.rs.core.HttpHeaders

internal class PdlOppslagServiceImplTest {
    @Rule
    val subject = SubjectRule(Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))
    val userToken = "RND-USER-TOKEN"
    val systemuserToken = "RND-STS-TOKEN"
    val stsMock: SystemUserTokenProvider = mock()

    @Before
    fun before() {
        whenever(stsMock.systemUserToken).thenReturn(systemuserToken)
    }

    @Test
    fun `riktige user-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifyUserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(stsMock, client).hentIdent("fnr")
        }
    }

    @Test
    fun `riktige system-headere skal settes på requesten`() {
        val client = createMockGraphQLClient { request ->
            verifySystemuserTokenHeaders(request)
            respond("{}", HttpStatusCode.OK)
        }

        TestUtils.withEnv("PDL_API_URL", "http://dummy.no") {
            PdlOppslagServiceImpl(stsMock, client).hentNavnBolk(listOf("fnr"))
        }
    }

    fun verifyUserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.NAV_CONSUMER_TOKEN_HEADER])
        assertEquals("Bearer $userToken", request.headers[HttpHeaders.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    fun verifySystemuserTokenHeaders(request: HttpRequestData) {
        assertNotNull(request.headers[RestConstants.NAV_CALL_ID_HEADER], "NAV_CALL_ID_HEADER missing")
        assertEquals("Bearer $systemuserToken", request.headers[RestConstants.NAV_CONSUMER_TOKEN_HEADER])
        assertEquals("Bearer $systemuserToken", request.headers[HttpHeaders.AUTHORIZATION])
        assertEquals(ALLE_TEMA_HEADERVERDI, request.headers[RestConstants.TEMA_HEADER])
    }

    fun createMockGraphQLClient(handler: MockRequestHandleScope.(request: HttpRequestData) -> HttpResponseData): GraphQLClient<*> {
        return GraphQLClient(url = URL("http://dummy.no"), engineFactory = MockEngine, configuration = {
            engine {
                addHandler { handler.invoke(this, it) }
            }
        })
    }
}

