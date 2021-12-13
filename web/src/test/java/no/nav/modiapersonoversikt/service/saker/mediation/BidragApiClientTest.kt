package no.nav.modiapersonoversikt.service.saker.mediation

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.RolleDto
import no.nav.modiapersonoversikt.utils.WireMockUtils
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BidragApiClientTest {
    private val TEST_SUBJECT = AuthContext(
        UserRole.INTERN,
        PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
    )

    @Language("json")
    val bisysResponse: String = """
        [
          {
            "eierfogd": "eierfogd",
            "saksnummer": "saksnummer-1",
            "saksstatus": "saksstatus",
            "kategori": "kategori",
            "erParagraf19": true,
            "begrensetTilgang": true,
            "roller": [
              { "foedselsnummer": "12345678910", "rolleType": "rolleType" },            
              { "foedselsnummer": "12345678910", "rolleType": "rolleType" }            
            ]
          },
          {
            "eierfogd": "eierfogd",
            "saksnummer": "saksnummer-2",
            "saksstatus": "saksstatus",
            "kategori": "kategori",
            "erParagraf19": true,
            "begrensetTilgang": true,
            "roller": [
              { "foedselsnummer": "12345678910", "rolleType": "rolleType" },            
              { "foedselsnummer": "12345678910", "rolleType": "rolleType" }            
            ]
          }
        ]   
    """.trimIndent()

    @Test
    fun `kan hente data fra bisys`() {
        WireMockUtils.withMockGateway(
            stub = WireMockUtils.getWithBody(statusCode = 200, body = bisysResponse),
            verify = {}
        ) { url ->
            AuthContextHolderThreadLocal.instance().withContext(TEST_SUBJECT) {
                val client = BidragSakControllerApi(url, BidragApiClient.client)
                val saker = client.find("12345678910")
                assertEquals(2, saker.size)
                assertEquals(BidragSakDto::class, saker[0]::class)
                assertEquals(RolleDto::class, saker[0].roller[0]::class)
            }
        }
    }
}
