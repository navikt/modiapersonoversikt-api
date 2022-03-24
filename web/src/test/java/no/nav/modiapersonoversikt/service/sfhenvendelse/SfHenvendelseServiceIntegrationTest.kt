package no.nav.modiapersonoversikt.service.sfhenvendelse

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.utils.Utils
import no.nav.modiapersonoversikt.utils.WireMockUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

internal class SfHenvendelseServiceIntegrationTest {
    private val TEST_SUBJECT = AuthContext(
        UserRole.INTERN,
        PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
    )

    val meldinger: String = SfHenvendelseServiceIntegrationTest::class.java.getResource("mock-sf-meldinger.json")
        ?.let { Files.readString(Path.of(it.toURI())) }!!

    @Test
    fun `kan hente meldinger`() {
        WireMockUtils.withMockGateway(
            stub = WireMockUtils.getWithBody(statusCode = 200, body = meldinger),
            verify = {}
        ) { url ->
            Utils.withProperty("SF_HENVENDELSE_URL", url) {
                AuthContextUtils.withContext(TEST_SUBJECT) {
                    val api = SfHenvendelseApiFactory.createHenvendelseInfoApi()
                    val result = api.henvendelseinfoHenvendelselisteGet("aktorid", "coorId")
                    assertThat(result).hasSize(1)
                }
            }
        }
    }
}
