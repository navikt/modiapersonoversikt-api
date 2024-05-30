package no.nav.modiapersonoversikt.rest.journalforing

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

@WebMvcTest(JournalforingControllerV2::class)
internal class JournalforingControllerTest {
    companion object {
        val ident = "Z999643"
        val sak =
            JournalforingSak().apply {
                temaKode = "DAG"
                fagsystemKode = "IT01"
                fagsystemSaksId = "987654"
            }

        val sakerService: SakerService = mockk()
        val sfHenvendelseService: SfHenvendelseService = mockk()
        val controller =
            JournalforingController(
                sakerService,
                sfHenvendelseService,
                TilgangskontrollMock.get(),
            )
    }

    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun tilgangskontroll(): Tilgangskontroll = TilgangskontrollMock.get()

        @Bean
        open fun sfHenvendelseService(): SfHenvendelseService = sfHenvendelseService

        @Bean
        open fun sakerService(): SakerService = sakerService
    }

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `knytter til sak og returnerer 200 OK`() {
        every { sfHenvendelseService.journalforHenvendelse(any(), any(), any(), any(), any()) } just runs

        AuthContextTestUtils.withIdent(
            ident,
            UnsafeSupplier {
                controller.knyttTilSak("10108000398", "traad-id", sak, "1234")
            },
        )
    }

    @Test
    fun `forespørsler som fieler kaster 500 Internal Server Error`() {
        every {
            sfHenvendelseService.journalforHenvendelse(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws IllegalStateException("Something went wrong")

        val exception =
            assertThrows<ResponseStatusException> {
                AuthContextTestUtils.withIdent(
                    ident,
                    UnsafeSupplier {
                        controller.knyttTilSak("10108000398", "traad-id", sak, "1234")
                    },
                )
            }

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.statusCode)
        assertEquals(JournalforingController.UKJENT_FEIL, exception.reason)
    }

    @Test
    fun `forespørsler uten enhet kaster 500 Internal Server Error med message satt i body`() {
        every { sfHenvendelseService.journalforHenvendelse(any(), any(), any(), any(), any()) } just runs

        val exception =
            assertThrows<ResponseStatusException> {
                AuthContextTestUtils.withIdent(
                    ident,
                    UnsafeSupplier {
                        controller.knyttTilSak("10108000398", "traad-id", sak, null)
                    },
                )
            }

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.statusCode)
        assertEquals(JournalforingController.FEILMELDING_UTEN_ENHET, exception.reason)
    }

    @Test
    internal fun `serialiserer saker riktig`() {
        every { sakerService.hentSaker(any()) } returns
            SakerService.Resultat(
                saker = mutableListOf(sak),
                feiledeSystemer = mutableListOf(),
            )

        mvc.post("/rest/v2/journalforing/saker/") {
            content = "{\"fnr\": \"10108000398\"}"
            contentType = MediaType.APPLICATION_JSON
        }.andExpect { jsonPath("$.saker[0].saksIdVisning", `is`(sak.fagsystemSaksId)) }
    }
}
