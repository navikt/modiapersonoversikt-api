package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.*
import org.joda.time.LocalDateTime
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(InnkrevingskravController::class)
class InnkrevingskravControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var innkrevingskravService: InnkrevingskravService

    @Autowired
    lateinit var tilgangskontroll: Tilgangskontroll

    private val innkrevingskrav =
        Krav(
            kravType = "kravType",
            kravId = "1234",
            kid = "12232424232323",
            debitor =
                Debitor(
                    debitorId = "12345",
                    name = "Debitor 1",
                    identType = IdentType.FNR,
                    ident = "10108000398",
                ),
            kreditor =
                Kreditor(
                    kreditorId = "12345",
                    name = "Debitor 1",
                    identType = IdentType.ORG_NR,
                    ident = "101080003",
                ),
            posteringer =
                listOf(
                    KravPostering(
                        kode = "KRL1",
                        beskrivelse = "Beskrivelse 3",
                        opprinneligBelop = 1000.0,
                        betaltBelop = 500.0,
                        gjenstaendeBelop = 500.0,
                        opprettetDato = LocalDateTime.parse("2024-05-14T12:00:00"),
                    ),
                ),
            opprettetDato = LocalDateTime.parse("2024-05-14T12:00:00"),
        )

    @Test
    fun `get kravdetaljer med eksisterende krav returnerer kravdetaljer`() {
        every { innkrevingskravService.hentInnkrevingskrav(any()) } returns innkrevingskrav

        mockMvc
            .get("/rest/innkrevingskrav/kravidentifikator") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType("application/json") }
                // language=json
                content {
                    json(
                        """
                        {
                            "kravType": "kravType",
                            "kravId": "1234",
                            "kid": "12232424232323",
                            "debitor": {
                            "debitorId": "12345",
                            "name": "Debitor 1",
                            "identType": "FNR",
                            "ident": "10108000398"
                        },
                            "kreditor": {
                            "kreditorId": "12345",
                            "name": "Debitor 1",
                            "identType": "ORG_NR",
                            "ident": "101080003"
                        },
                            "posteringer": [
                            {
                                "kode": "KRL1",
                                "beskrivelse": "Beskrivelse 3",
                                "opprinneligBelop": 1000.0,
                                "betaltBelop": 500.0,
                                "gjenstaendeBelop": 500.0,
                                "opprettetDato": "2024-05-14T12:00:00.000"
                            }
                            ],
                            "opprettetDato": "2024-05-14T12:00:00.000"
                        }
                        """.trimIndent(),
                    )
                }
            }
    }

    @Test
    fun `get kravdetaljer med ikke eksisterende krav returnerer null`() {
        every { innkrevingskravService.hentInnkrevingskrav(any()) } returns null

        mockMvc
            .get("/rest/innkreving/kravdetaljer/kravidentifikator") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `get alle krav for en person returnerer liste med alle krav`() {
        every { innkrevingskravService.hentAlleInnkrevingskrav(any()) } returns listOf(innkrevingskrav)

        mockMvc
            .post("/rest/innkrevingskrav") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(FnrRequest("12345678910"))
            }.andExpect {
                status { isOk() }
                content { contentType("application/json") }
                // language=json
                content {
                    json(
                        """
                        [
                            {
                                "kravType": "kravType",
                                "kravId": "1234",
                                "kid": "12232424232323",
                                "debitor": {
                                        "debitorId": "12345",
                                        "name": "Debitor 1",
                                        "identType": "FNR",
                                        "ident": "10108000398"
                                    },
                                "kreditor": {
                                        "kreditorId": "12345",
                                        "name": "Debitor 1",
                                        "identType": "ORG_NR",
                                        "ident": "101080003"
                                    },
                                "posteringer": [
                                        {
                                            "kode": "KRL1",
                                            "beskrivelse": "Beskrivelse 3",
                                            "opprinneligBelop": 1000.0,
                                            "betaltBelop": 500.0,
                                            "gjenstaendeBelop": 500.0,
                                            "opprettetDato": "2024-05-14T12:00:00.000"
                                        }
                                    ],
                                "opprettetDato": "2024-05-14T12:00:00.000"
                            }
                        ]
                        """.trimIndent(),
                    )
                }
            }
    }

    @Test
    fun `get alle krav for en person returnerer 400 hvis personident ikke er gyldig`() {
        mockMvc
            .post("/rest/innkrevingskrav") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(FnrRequest("1"))
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun kravService(): InnkrevingskravService = mockk()

        @Bean
        open fun tilgangskontroll(): Tilgangskontroll = TilgangskontrollMock
    }
}
