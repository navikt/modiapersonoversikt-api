package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.config.endpoint.Utils
import no.nav.modiapersonoversikt.utils.WireMockUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

internal class SfHenvendelseServiceIntegrationTest {
    private val TEST_SUBJECT =
        Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>()))

    val meldinger: String = SfHenvendelseServiceIntegrationTest::class.java.getResource("mock-sf-meldinger.json")
        ?.let { Files.readString(Path.of(it.toURI())) }!!

    @Test
    fun `kan hente meldinger`() {
        WireMockUtils.withMockGateway(
            stub = WireMockUtils.getWithBody(statusCode = 200, body = meldinger),
            verify = {}
        ) { url ->
            Utils.withProperty("SF_HENVENDELSE_URL", url) {
                SubjectHandler.withSubject(TEST_SUBJECT) {
                    val api = SfHenvendelseApiFactory.createHenvendelseInfoApi()
                    val result = api.henvendelseinfoHenvendelselisteGet("aktorid", "coorId")
                    assertThat(result).hasSize(1)
                }
            }
        }
    }
}
