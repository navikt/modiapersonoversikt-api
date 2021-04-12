package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.HttpRequestUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class OppgaveControllerTest {
    private val plukkOppgaveService: PlukkOppgaveService = mockk()
    private val henvendelseUtsendingService: HenvendelseUtsendingService = mockk()
    private val oppgavebehandlingService: OppgaveBehandlingService = mockk();
    private val oppgaveController: OppgaveController = OppgaveController(
        oppgavebehandlingService,
        plukkOppgaveService,
        henvendelseUtsendingService,
        TilgangskontrollMock.get()
    )

    @Test
    fun `Legger tilbake oppgave ved å kalle lagreOppgave mot GSAK`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)
        every { oppgavebehandlingService.leggTilbakeOppgaveIGsak(any()) } returns Unit
        every { henvendelseUtsendingService.oppdaterTemagruppe(any(), any()) } returns Unit

        val leggTilbakeRequest = LeggTilbakeRequest(
            enhet = null,
            oppgaveId = OPPGAVE_ID_1,
            type = LeggTilbakeAarsak.FeilTema,
            beskrivelse = null,
            temagruppe = Temagruppe.ARBD,
            traadId = "123456"
        )

        SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT) {
            oppgaveController.leggTilbake(httpRequest, leggTilbakeRequest)
        }

        verifySequence {
            oppgavebehandlingService.leggTilbakeOppgaveIGsak(
                LeggTilbakeOppgaveIGsakRequest()
                    .withOppgaveId(OPPGAVE_ID_1)
                    .withSaksbehandlersValgteEnhet(VALGT_ENHET)
                    .withBeskrivelse("Oppgave lagt tilbake. Årsak: feil temagruppe")
                    .withTemagruppe(Temagruppe.ARBD)
            )
            henvendelseUtsendingService.oppdaterTemagruppe("123456", "ARBD")
        }
    }

    @Test
    fun `Returnerer tildelte oppgaver`() {
        val oppgaveliste = listOf(
            Oppgave(OPPGAVE_ID_1, "fnr", "traadId", true),
            Oppgave(OPPGAVE_ID_2, "fnr", "traadId", true)
        )

        every { oppgavebehandlingService.finnTildelteOppgaverIGsak() } returns oppgaveliste
        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier {
            oppgaveController.finnTildelte()
        })

        assertEquals(oppgaveliste.size, resultat.size)
        assertEquals(oppgaveliste[0].oppgaveId, resultat[0].oppgaveId)
        assertEquals(oppgaveliste[1].oppgaveId, resultat[1].oppgaveId)
    }

    @Test
    fun `Returnerer oppgaver ved plukk`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)
        val oppgaver = listOf(
            Oppgave(OPPGAVE_ID_1, "fnr", "traadId", true),
            Oppgave(OPPGAVE_ID_2, "fnr", "traadId", true)
        )

        every { oppgavebehandlingService.finnTildelteOppgaverIGsak() } returns mutableListOf()
        every { plukkOppgaveService.plukkOppgaver(any(), any()) } returns oppgaver

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier {
            oppgaveController.plukkOppgaver(TEMAGRUPPE_ARBEID, null, httpRequest)
        })

        assertEquals(oppgaver.size, resultat.size)
        assertEquals(oppgaver[0].oppgaveId, resultat[0].oppgaveId)
        assertEquals(oppgaver[1].oppgaveId, resultat[1].oppgaveId)
    }

    @Test
    fun `Returnerer tildelt oppgave hvis saksbehandler allerede har en tildelt oppgave ved plukk`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)
        val oppgaveliste = listOf(
            Oppgave(OPPGAVE_ID_1, "fnr", "id", true),
            Oppgave("2", "1234", "id", true)
        )
        every { oppgavebehandlingService.finnTildelteOppgaverIGsak() } returns oppgaveliste

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier {
            oppgaveController.plukkOppgaver(TEMAGRUPPE_ARBEID, null, httpRequest)
        })

        verify(exactly = 0) {
            plukkOppgaveService.plukkOppgaver(any(), any())
        }
        assertEquals(oppgaveliste.size, resultat.size)
        assertEquals(oppgaveliste[0].oppgaveId, resultat[0].oppgaveId)
    }

    @Test
    fun `Returnerer tom liste hvis tjenesten returnerer tom liste`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)
        every { oppgavebehandlingService.finnTildelteOppgaverIGsak() } returns mutableListOf()
        every { plukkOppgaveService.plukkOppgaver(any(), any()) } returns mutableListOf()

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier {
            oppgaveController.plukkOppgaver(TEMAGRUPPE_ARBEID, null, httpRequest)
        })

        assertEquals(0, resultat.size)
    }

    companion object {
        const val OPPGAVE_ID_1 = "OPPGAVE_ID_1"
        const val OPPGAVE_ID_2 = "OPPGAVE_ID_2"
        const val SAKSBEHANDLERS_IDENT = "SAKSBEHANDLER"
        const val TEMAGRUPPE_ARBEID = "ARBD"
        const val VALGT_ENHET = "4300"
    }
}
