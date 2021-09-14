package no.nav.modiapersonoversikt.rest.kodeverk

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@WebMvcTest(KodeverkController::class)
internal class KodeverkControllerTest {
    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun tilgangskontroll(): Tilgangskontroll = TilgangskontrollMock.get()

        @Bean
        open fun legacyKodeverk(): KodeverkmanagerBi = mockk()
    }

    @MockkBean
    lateinit var kodeverkMock: EnhetligKodeverk.Service

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    internal fun `skal returnere kodeverk`() {
        val kodeverk = mapOf(
            "kode1" to "verdi1",
            "kode2" to "verdi2",
            "kode3" to "verdi3"
        )
        every { kodeverkMock.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(
            navn = "kodeverksnavn",
            kodeverk = kodeverk
        )
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/rest/v2/kodeverk/LAND")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect { result ->
                assertThat(result.response.status).isEqualTo(200)
                assertThat(result.response.contentAsString).isEqualTo(mapper.writeValueAsString(kodeverk))
            }
    }
}
