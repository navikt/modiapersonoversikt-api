package no.nav.modiapersonoversikt.rest.featuretoggle

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.testutils.WebMvcTestUtils.getJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(FeatureToggleController::class)
internal class FeatureToggleControllerTest {
    companion object {
        private val unleashService: UnleashService = mockk()
    }

    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun tilgangskontroll(): Tilgangskontroll = TilgangskontrollMock.get()

        @Bean
        open fun unleash(): UnleashService = unleashService
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    internal fun setUp() {
        every { unleashService.isEnabled(eq("min.id")) } returns true
        every { unleashService.isEnabled(eq("annen.id")) } returns false
        every { unleashService.isEnabled(eq("tredje.id")) } returns true
    }

    @Test
    internal fun `skal evaluere enkelt toggle`() {
        mockMvc
            .getJson("/rest/featuretoggle/min.id")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                assertThat(it.response.contentAsString).isEqualTo("true")
            }

        mockMvc
            .getJson("/rest/featuretoggle/annen.id")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                assertThat(it.response.contentAsString).isEqualTo("false")
            }
    }

    @Test
    internal fun `skal hente alle toggles spesifisert som queryparams`() {
        mockMvc
            .getJson("/rest/featuretoggle/?id=min.id&id=annen.id&id=tredje.id")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                assertThat(it.response.contentAsString).isEqualTo("""{"min.id":true,"annen.id":false,"tredje.id":true}""")
            }
    }

    @Test
    internal fun `skal returnere tomt map om ingen featuretoggles er spesifisert`() {
        mockMvc
            .getJson("/rest/featuretoggle")
            .andExpect {
                assertThat(it.response.status).isEqualTo(200)
                assertThat(it.response.contentAsString).isEqualTo("{}")
            }
    }
}
