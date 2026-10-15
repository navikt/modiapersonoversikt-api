package no.nav.modiapersonoversikt.rest.arena

import no.nav.modiapersonoversikt.config.JavaTimeCompatConfig
import no.nav.modiapersonoversikt.consumer.arena.sakVedtakService.JournalforingSak
import no.nav.modiapersonoversikt.consumer.arena.sakVedtakService.SakVedtakService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.OffsetDateTime

@WebMvcTest(SakVedtakController::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JavaTimeCompatConfig::class, SakVedtakDateTimeCompatibilityTest.TestConfig::class)
internal class SakVedtakDateTimeCompatibilityTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun sakVedtakService(): SakVedtakService =
            object : SakVedtakService {
                override fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak =
                    JournalforingSak().apply {
                        opprettetDato = OffsetDateTime.parse("2024-01-15T10:30:00.000Z")
                    }
            }
    }

    @Test
    fun `serves journal timestamps using the existing millisecond ISO format`() {
        mockMvc
            .perform(
                post("/rest/sakvedtak")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""""12345678901""""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.opprettetDato").value("2024-01-15T10:30:00.000Z"))
    }
}
