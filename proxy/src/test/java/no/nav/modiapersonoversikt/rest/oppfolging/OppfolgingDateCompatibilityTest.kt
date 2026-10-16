package no.nav.modiapersonoversikt.rest.oppfolging

import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.OppfolgingskontraktResponse
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktRequest
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService
import no.nav.modiapersonoversikt.infrastructure.OkHttpUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

internal class OppfolgingDateCompatibilityTest {
    @Test
    fun `parses date-only input and returns date-only JSON`() {
        val service = DateResponseService()
        val mockMvc = createMockMvc(service)

        mockMvc
            .perform(
                post("/rest/Oppfolgingskontrakter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"fnr":"12345678901","start":"2024-02-29","slutt":"2024-03-01"}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.vedtaksdato").value("2024-02-29"))

        assertEquals("2024-02-29", service.request?.from.toString())
        assertEquals("2024-03-01", service.request?.to.toString())
    }

    private fun createMockMvc(service: OppfolgingskontraktService): MockMvc =
        MockMvcBuilders
            .standaloneSetup(OppfolgingController(service))
            .setMessageConverters(MappingJackson2HttpMessageConverter(OkHttpUtils.objectMapper))
            .build()

    private class DateResponseService : OppfolgingskontraktService {
        var request: OppfolgingskontraktRequest? = null
        private val response = OppfolgingskontraktResponse()

        init {
            val dateType = response.javaClass.getMethod("getVedtaksdato").returnType
            val parseMethod = dateType.methods.single { it.name == "parse" && it.parameterCount == 1 }
            val date = parseMethod.invoke(null, "2024-02-29")
            response.javaClass.getMethod("setVedtaksdato", dateType).invoke(response, date)
        }

        override fun hentOppfolgingskontrakter(request: OppfolgingskontraktRequest): OppfolgingskontraktResponse {
            this.request = request
            return response
        }
    }
}
