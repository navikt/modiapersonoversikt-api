package no.nav.modiapersonoversikt.service.sfhenvendelse

import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.Utils
import no.nav.modiapersonoversikt.utils.WireMockUtils.get
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import no.nav.modiapersonoversikt.utils.readResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SfHenvendelseServiceIntegrationTest {
    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance().build()

        private val testSubject =
            AuthContext(
                UserRole.INTERN,
                PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build()),
            )
    }

    private val meldinger: String = SfHenvendelseServiceIntegrationTest.readResource("mock-sf-meldinger.json")

    @Test
    fun `kan hente meldinger`() {
        wiremock.get {
            status(200)
            json(meldinger)
        }
        Utils.withProperty("SF_HENVENDELSE_URL", "http://localhost:${wiremock.port}") {
            val oboTokenProvider = mockk<BoundedOnBehalfOfTokenClient>()
            every { oboTokenProvider.exchangeOnBehalfOfToken(testSubject.idToken.serialize()) } returns "OBO-TOKEN"

            AuthContextUtils.withContext(testSubject) {
                val api = SfHenvendelseApiFactory.createHenvendelseInfoApi(oboTokenProvider)
                val result = api.henvendelseinfoHenvendelselisteGet("aktorid", "coorId")
                assertThat(result).hasSize(1)
            }
        }
    }
}
