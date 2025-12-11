package no.nav.modiapersonoversikt.rest.oppgave

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

internal class OppgaveControllerTest {
    private val oppgavebehandlingService: OppgaveBehandlingService = mockk()
    private val oppgaveController =
        OppgaveController(
            oppgavebehandlingService,
            TilgangskontrollMock.get(),
        )

    @Test
    fun `Returnerer tildelte oppgaver`() {
        val oppgaveliste =
            listOf(
                Oppgave(
                    OPPGAVE_ID_1,
                    "fnr",
                    "traadId",
                    true,
                    tildeltEnhetsnr = "0001",
                    tema = TEMA_AAP,
                    temagruppe = TEMAGRUPPE_ARBEID,
                    oppgavetype = "MLD",
                    prioritet = "1",
                    status = "OPPRETTET",
                    aktivDato = LocalDate.now(),
                    endretAvEnhetsnr = VALGT_ENHET,
                    opprettetAvEnhetsnr = VALGT_ENHET,
                    saksreferanse = "",
                    beskrivelse = "",
                ),
                Oppgave(
                    OPPGAVE_ID_2,
                    "fnr",
                    "traadId",
                    true,
                    tildeltEnhetsnr = "0001",
                    tema = TEMA_AAP,
                    temagruppe = TEMAGRUPPE_ARBEID,
                    oppgavetype = "MLD",
                    prioritet = "1",
                    status = "OPPRETTET",
                    aktivDato = LocalDate.now(),
                    endretAvEnhetsnr = VALGT_ENHET,
                    opprettetAvEnhetsnr = VALGT_ENHET,
                    saksreferanse = "",
                    beskrivelse = "",
                ),
            )

        every { oppgavebehandlingService.finnTildelteOppgaverIGsak("") } returns oppgaveliste
        val resultat =
            AuthContextTestUtils.withIdent(
                SAKSBEHANDLERS_IDENT,
                UnsafeSupplier {
                    oppgaveController.finnTildelte(FnrRequest(""))
                },
            )

        assertEquals(oppgaveliste.size, resultat.size)
        assertEquals(oppgaveliste[0].oppgaveId, resultat[0].oppgaveId)
        assertEquals(oppgaveliste[1].oppgaveId, resultat[1].oppgaveId)
    }

    @Test
    fun `skal returnere oppgavedata for gitt oppgaveId`() {
        val oppgaveDTO =
            Oppgave(
                OPPGAVE_ID_1,
                "fnr",
                "traadId",
                true,
                tildeltEnhetsnr = "0001",
                tema = TEMA_AAP,
                temagruppe = TEMAGRUPPE_ARBEID,
                oppgavetype = "MLD",
                prioritet = "1",
                status = "OPPRETTET",
                aktivDato = LocalDate.now(),
                endretAvEnhetsnr = VALGT_ENHET,
                opprettetAvEnhetsnr = VALGT_ENHET,
                saksreferanse = "",
                beskrivelse = "",
            )

        every { oppgavebehandlingService.hentOppgave(OPPGAVE_ID_1) } returns oppgaveDTO

        val resultat =
            AuthContextTestUtils.withIdent(
                SAKSBEHANDLERS_IDENT,
                UnsafeSupplier {
                    oppgaveController.getOppgaveData(OPPGAVE_ID_1)
                },
            )

        assertEquals(OPPGAVE_ID_1, resultat.oppgaveId)
        assertEquals("fnr", resultat.fnr)
        verify { oppgavebehandlingService.hentOppgave(OPPGAVE_ID_1) }
    }

    @Test
    fun `skal kaste feil hvis oppgaven ikke finnes`() {
        every { oppgavebehandlingService.hentOppgave(OPPGAVE_ID_1) } throws NoSuchElementException("Oppgave ikke funnet")

        AuthContextTestUtils.withIdent(
            SAKSBEHANDLERS_IDENT,
            UnsafeSupplier {
                try {
                    oppgaveController.getOppgaveData(OPPGAVE_ID_1)
                } catch (e: NoSuchElementException) {
                    assertEquals("Oppgave ikke funnet", e.message)
                }
            },
        )

        verify { oppgavebehandlingService.hentOppgave(OPPGAVE_ID_1) }
    }

    companion object {
        const val OPPGAVE_ID_1 = "OPPGAVE_ID_1"
        const val OPPGAVE_ID_2 = "OPPGAVE_ID_2"
        const val SAKSBEHANDLERS_IDENT = "SAKSBEHANDLER"
        const val TEMAGRUPPE_ARBEID = "ARBD"
        const val TEMA_AAP = "TEMA_AAP"
        const val VALGT_ENHET = "4300"
    }
}
