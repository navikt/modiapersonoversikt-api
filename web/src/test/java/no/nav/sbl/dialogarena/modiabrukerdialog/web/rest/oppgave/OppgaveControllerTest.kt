package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt
import com.nhaarman.mockitokotlin2.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.HttpRequestUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.AnsattServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.OppgaveBehandlingServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService
import no.nav.sbl.util.fn.UnsafeSupplier
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.*
import javax.ws.rs.ForbiddenException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class OppgaveControllerTest {
    private val oppgaveBehandlingMock: OppgavebehandlingV3 = mock()
    private val tildelOppgaveMock: TildelOppgaveV1 = mock()
    private val oppgaveWSMock: OppgaveV3 = mockOppgaveWs()
    private val ansattWSMock: AnsattServiceImpl = AnsattServiceImpl(mockGosysNavAnsatt())
    private val arbeidsfordelingV1Service: ArbeidsfordelingV1Service = mock()
    private val plukkOppgaveService: PlukkOppgaveService = mock()
    private val ldapService: LDAPService = mock()
    private val henvendelseUtsendingService: HenvendelseUtsendingService = mock()
    private val oppgaveController: OppgaveController = OppgaveController(
            OppgaveBehandlingServiceImpl(
                    oppgaveBehandlingMock,
                    tildelOppgaveMock,
                    oppgaveWSMock,
                    ansattWSMock,
                    arbeidsfordelingV1Service,
                    TilgangskontrollMock.get()
            ),
            plukkOppgaveService,
            ldapService,
            henvendelseUtsendingService,
            TilgangskontrollMock.get()
    )

    @BeforeEach
    fun before() {
        whenever(ldapService.saksbehandlerHarRolle(any(), any())).thenReturn(true)
    }

    private fun mockOppgaveWs(): OppgaveV3 {
        val oppgaveMock: OppgaveV3 = mock()
        whenever(oppgaveMock.hentOppgave(any()))
                .thenReturn(WSHentOppgaveResponse().withOppgave(mockOppgaveFraGSAK()))
        return oppgaveMock
    }

    private fun mockGosysNavAnsatt(): GOSYSNAVansatt {
        val gosysNAVAnsatt: GOSYSNAVansatt = mock()
        whenever(gosysNAVAnsatt.hentNAVAnsatt(any())).thenReturn(ASBOGOSYSNAVAnsatt())
        whenever(gosysNAVAnsatt.hentNAVAnsattEnhetListe(any()))
                .thenReturn(ASBOGOSYSNAVEnhetListe().apply { navEnheter.add(ASBOGOSYSNavEnhet()) })
        return gosysNAVAnsatt
    }

    private fun mockOppgaveFraGSAK() = WSOppgave()
            .withAnsvarligId(SAKSBEHANDLERS_IDENT)
            .withOppgaveId(OPPGAVE_ID_1)
            .withOppgavetype(WSOppgavetype())
            .withFagomrade(WSFagomrade())
            .withPrioritet(WSPrioritet())
            .withUnderkategori(WSUnderkategori())
            .withVersjon(5)
            .withLest(false)
            .withGjelder(WSBruker().withBrukerId(BRUKERS_FODSELSNUMMER))

    @Test
    fun `Legger tilbake oppgave ved å kalle lagreOppgave mot GSAK`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)

        SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT) {
            oppgaveController.leggTilbake(httpRequest, lagRequest())
        }

        verify(oppgaveBehandlingMock).lagreOppgave(check {
            assertAll("Oppgave lagret i GSAK",
                    Executable { assertEquals(OPPGAVE_ID_1, it.endreOppgave.oppgaveId) },
                    Executable { assertEquals("", it.endreOppgave.ansvarligId) },
                    Executable { assertEquals(UNDERKATEGORI_KODE_FOR_TEMAGRUPPE_ARBEID, it.endreOppgave.underkategoriKode) },
                    Executable { assertThat(it.endreOppgave.beskrivelse, containsString(VALGT_ENHET)) }
            )
        })
    }

    @Test
    fun `Sjekker at ansvarlig for oppgaven er samme person som forsøker å legge den tilbake`() {
        assertFailsWith<ForbiddenException> {
            val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie("annen-saksbehandler", VALGT_ENHET)
            SubjectHandlerUtil.withIdent("annen-saksbehandler") {
                oppgaveController.leggTilbake(httpRequest, lagRequest())
            }
        }
    }

    @Test
    fun `Returnerer tildelte oppgaver`() {
        val oppgaveliste = listOf(lagWSOppgave().withOppgaveId("1"), lagWSOppgave().withOppgaveId("2"))

        whenever(oppgaveWSMock.finnOppgaveListe(any()))
                .thenReturn(WSFinnOppgaveListeResponse()
                        .withOppgaveListe(oppgaveliste))

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier { oppgaveController.finnTildelte() })

        assertEquals(oppgaveliste.size, resultat.size)
        assertEquals(oppgaveliste[0].oppgaveId, resultat[0]["oppgaveId"])
        assertEquals(oppgaveliste[1].oppgaveId, resultat[1]["oppgaveId"])
    }

    @Test
    fun `Kaller finnOppgaveListe med riktig request`() {
        whenever(oppgaveWSMock.finnOppgaveListe(any())).thenReturn(WSFinnOppgaveListeResponse())
        SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT) { oppgaveController.finnTildelte() }
        verify(oppgaveWSMock).finnOppgaveListe(check {
            assertEquals(SAKSBEHANDLERS_IDENT, it.sok.ansvarligId)
            assertEquals(1, it.sok.fagomradeKodeListe.size)
            assertEquals("KNA", it.sok.fagomradeKodeListe[0])
            assertEquals(1, it.filter.oppgavetypeKodeListe.size)
            assertEquals("SPM_OG_SVR", it.filter.oppgavetypeKodeListe[0])
            assertTrue(it.filter.isAktiv)
        })
    }

    @Test
    fun `Returnerer oppgaver ved plukk`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)
        val oppgaver = listOf(
                Oppgave(OPPGAVE_ID_1, "fnr", "traadId", true),
                Oppgave(OPPGAVE_ID_2, "fnr", "traadId", true)
        )

        whenever(tildelOppgaveMock.tildelFlereOppgaver(any()))
                .thenReturn(WSTildelFlereOppgaverResponse().withOppgaveIder(1, 2))
        whenever(plukkOppgaveService.plukkOppgaver(any(), any()))
                .thenReturn(oppgaver)
        whenever(oppgaveWSMock.finnOppgaveListe(any()))
                .thenReturn(WSFinnOppgaveListeResponse())

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier { oppgaveController.plukkOppgaver(TEMAGRUPPE_ARBEID, httpRequest) })

        assertEquals(oppgaver.size, resultat.size)
        assertEquals(oppgaver[0].oppgaveId, resultat[0]["oppgaveId"])
        assertEquals(oppgaver[1].oppgaveId, resultat[1]["oppgaveId"])
    }

    @Test
    fun `Returnerer tildelt oppgave hvis saksbehandler allerede har en tildelt oppgave ved plukk`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)

        val oppgaveliste = listOf(lagWSOppgave().withOppgaveId(OPPGAVE_ID_1), lagWSOppgave().withOppgaveId("2").withGjelder(WSBruker().withBrukerId("1234")))

        whenever(oppgaveWSMock.finnOppgaveListe(any()))
                .thenReturn(WSFinnOppgaveListeResponse()
                        .withOppgaveListe(oppgaveliste))
        whenever(plukkOppgaveService.plukkOppgaver(any(), any()))
                .thenReturn(emptyList())

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier { oppgaveController.plukkOppgaver(TEMAGRUPPE_ARBEID, httpRequest) })

        verify(plukkOppgaveService, times(0)).plukkOppgaver(any(), any())
        assertEquals(oppgaveliste.size, resultat.size)
        assertEquals(oppgaveliste[0].oppgaveId, resultat[0]["oppgaveId"])
    }

    @Test
    fun `Returnerer tom liste hvis tjenesten returnerer tom liste`() {
        val httpRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET)

        whenever(tildelOppgaveMock.tildelFlereOppgaver(any()))
                .thenReturn(WSTildelFlereOppgaverResponse())
        whenever(oppgaveWSMock.finnOppgaveListe(any()))
                .thenReturn(WSFinnOppgaveListeResponse())

        val resultat = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, UnsafeSupplier { oppgaveController.plukkOppgaver(TEMAGRUPPE_ARBEID, httpRequest) })

        assertEquals(0, resultat.size)
    }

    private fun lagRequest() = LeggTilbakeRequest(oppgaveId = OPPGAVE_ID_1, type = LeggTilbakeAarsak.FeilTema, beskrivelse = null, temagruppe = Temagruppe.ARBD, traadId = "123456")

    companion object {
        const val OPPGAVE_ID_1 = "OPPGAVE_ID_1"
        const val OPPGAVE_ID_2 = "OPPGAVE_ID_2"
        const val BRUKERS_FODSELSNUMMER = "10108000398"
        const val SAKSBEHANDLERS_IDENT = "SAKSBEHANDLER"
        const val UNDERKATEGORI_KODE_FOR_TEMAGRUPPE_ARBEID = "ARBD_KNA"
        const val TEMAGRUPPE_ARBEID = "ARBD"
        const val VALGT_ENHET = "4300"
    }

    fun lagWSOppgave(): WSOppgave {
        return lagWSOppgave("1")
    }

    fun lagWSOppgave(oppgaveId: String): WSOppgave {
        return WSOppgave()
                .withOppgaveId(oppgaveId)
                .withHenvendelseId(UUID.randomUUID().toString())
                .withOppgavetype(WSOppgavetype().withKode("wsOppgavetype"))
                .withGjelder(WSBruker().withBrukerId("10108000398"))
                .withStatus(WSStatus().withKode("statuskode"))
                .withFagomrade(WSFagomrade().withKode("HJE"))
                .withAktivFra(DateTime.now().toLocalDate())
                .withPrioritet(WSPrioritet().withKode("NORM_GEN"))
                .withUnderkategori(WSUnderkategori().withKode("ARBEID_HJE"))
                .withLest(false)
                .withVersjon(1)

    }

}
