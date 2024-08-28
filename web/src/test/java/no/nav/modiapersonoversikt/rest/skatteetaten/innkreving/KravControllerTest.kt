package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Krav
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.KravService
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Kravdetaljer
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Kravgrunnlag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(KravController::class)
class KravControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var kravService: KravService

    private val kravdetaljer =
        Kravdetaljer(
            Kravgrunnlag(null),
            listOf(
                Krav(
                    "kravType",
                    200.0,
                    100.0,
                ),
            ),
        )

    @Test
    fun `get kravdetaljer med eksisterende krav returnerer kravdetaljer`() {
        every { kravService.hentKrav(any()) } returns kravdetaljer

        mockMvc
            .get("/rest/skatteetaten/innkreving/kravdetaljer/kravidentifikator") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType("application/json") }
                // language=json
                content {
                    json(
                        """
                        {
                            "kravgrunnlag": {
                                "datoNaarKravVarBesluttetHosOppdragsgiver": null
                            },
                            "krav": [
                                {
                                    "kravType": "kravType",
                                    "opprinneligBeløp": 200.0,
                                    "gjenståendeBeløp": 100.0
                                }
                            ]
                        }
                        """.trimIndent(),
                    )
                }
            }
    }

    @Test
    fun `get kravdetaljer med ikke eksisterende krav returnerer null`() {
        every { kravService.hentKrav(any()) } returns null

        mockMvc
            .get("/rest/skatteetaten/innkreving/kravdetaljer/kravidentifikator") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isNotFound() }
            }
    }

    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun kravService(): KravService = mockk()
    }
}
