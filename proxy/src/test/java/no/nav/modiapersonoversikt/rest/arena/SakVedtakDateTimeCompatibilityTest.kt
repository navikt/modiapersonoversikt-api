package no.nav.modiapersonoversikt.rest.arena

import no.nav.modiapersonoversikt.consumer.arena.sakVedtakService.JournalforingSak
import no.nav.modiapersonoversikt.consumer.arena.sakVedtakService.SakVedtakService
import no.nav.modiapersonoversikt.infrastructure.OkHttpUtils
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

internal class SakVedtakDateTimeCompatibilityTest {
    @Test
    fun `serves journal timestamps using the existing millisecond ISO format`() {
        val sak = JournalforingSak()
        val dateField = sak.javaClass.getField("opprettetDato")
        val parseMethod = dateField.type.methods.single { it.name == "parse" && it.parameterCount == 1 }
        dateField.set(sak, parseMethod.invoke(null, "2024-01-15T10:30:00.000Z"))
        val service =
            object : SakVedtakService {
                override fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak = sak
            }
        val mockMvc =
            MockMvcBuilders
                .standaloneSetup(SakVedtakController(service))
                .setMessageConverters(MappingJackson2HttpMessageConverter(OkHttpUtils.objectMapper))
                .build()

        mockMvc
            .perform(
                post("/rest/sakvedtak")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""""12345678901""""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.opprettetDato").value("2024-01-15T10:30:00.000Z"))
    }
}
