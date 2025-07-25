package no.nav.modiapersonoversikt.rest.oppgave

import io.mockk.every
import io.mockk.mockk
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import org.junit.jupiter.api.Test
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
                ),
                Oppgave(
                    OPPGAVE_ID_2,
                    "fnr",
                    "traadId",
                    true,
                ),
            )

        every { oppgavebehandlingService.finnTildelteOppgaverIGsak() } returns oppgaveliste
        val resultat =
            AuthContextTestUtils.withIdent(
                SAKSBEHANDLERS_IDENT,
                UnsafeSupplier {
                    oppgaveController.finnTildelte()
                },
            )

        assertEquals(oppgaveliste.size, resultat.size)
        assertEquals(oppgaveliste[0].oppgaveId, resultat[0].oppgaveId)
        assertEquals(oppgaveliste[1].oppgaveId, resultat[1].oppgaveId)
    }

    companion object {
        const val OPPGAVE_ID_1 = "OPPGAVE_ID_1"
        const val OPPGAVE_ID_2 = "OPPGAVE_ID_2"
        const val SAKSBEHANDLERS_IDENT = "SAKSBEHANDLER"
        const val TEMAGRUPPE_ARBEID = "ARBD"
        const val VALGT_ENHET = "4300"
    }
}
