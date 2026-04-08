package no.nav.modiapersonoversikt.rest.lumi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.consumer.lumi.LumiService
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.junit.jupiter.api.Test

internal class LumiControllerTest {
    private val lumiService: LumiService = mockk(relaxed = true)
    private val controller = LumiController(lumiService, TilgangskontrollMock.get())

    @Test
    fun `sender transport payload videre til lumi service`() {
        AuthContextTestUtils.withIdent(
            SAKSBEHANDLER,
            UnsafeSupplier {
                controller.submitFeedback(TRANSPORT_PAYLOAD)
            },
        )

        verify(exactly = 1) {
            lumiService.submitFeedback(TRANSPORT_PAYLOAD)
        }
    }

    companion object {
        private const val SAKSBEHANDLER = "Z999999"
        private val TRANSPORT_PAYLOAD =
            jacksonObjectMapper().readTree(
                """{"surveyId":"survey-1","answers":[{"question":"q1","value":"yes"}]}""",
            )
    }
}
