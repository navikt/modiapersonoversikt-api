package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kjerneinfo.consumer.fim.behandleperson.DefaultBehandlePersonService
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse
import no.nav.kjerneinfo.domain.person.Fodselsnummer
import no.nav.kjerneinfo.domain.person.Person
import no.nav.kjerneinfo.domain.person.Personfakta
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil.domain.EndreNavnRequest
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest
import org.junit.jupiter.api.*
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import javax.ws.rs.ForbiddenException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val AREMARK_FNR = "10108000398"
private val D_NUMMER = "50108000398"
private val INNLOGGET_SAKSBEHANDLER = "z111111"

private val FORNAVN = "Peter"
private val MELLOMNAVN = "Wessel"
private val ETTERNAVN = "Zapffe"

class BrukerprofilControllerTest {

    @Mock private lateinit var behandlePersonService: DefaultBehandlePersonService
    @Mock private lateinit var kjerneinfoService: DefaultPersonKjerneinfoService
    @Mock private lateinit var ldapService: LDAPService

    private lateinit var controller: BrukerprofilController

    @BeforeEach
    fun before() {
        MockitoAnnotations.initMocks(this);
        controller = BrukerprofilController(behandlePersonService, kjerneinfoService, ldapService)
        SubjectHandlerUtil.setInnloggetSaksbehandler(INNLOGGET_SAKSBEHANDLER)
        `when`(ldapService.saksbehandlerHarRolle(INNLOGGET_SAKSBEHANDLER, ENDRE_NAVN_ROLLE)).thenReturn(true)
    }

    @Test
    fun endrerNavn() {
        `when`(kjerneinfoService.hentKjerneinformasjon(Matchers.any())).thenReturn(mockKjerneinformasjonResponse(D_NUMMER))
        val argumentCaptor = ArgumentCaptor.forClass(WSEndreNavnRequest::class.java)

        val endreNavnRequest = lagRequest(D_NUMMER)
        controller.endreNavn(D_NUMMER, endreNavnRequest)

        verify(behandlePersonService, times(1)).endreNavn(argumentCaptor.capture())
        assertEquals(D_NUMMER, argumentCaptor.value.fnr)
        assertEquals(FORNAVN, argumentCaptor.value.fornavn)
        assertEquals(MELLOMNAVN, argumentCaptor.value.mellomnavn)
        assertEquals(ETTERNAVN, argumentCaptor.value.etternavn)
    }

    @Test
    @DisplayName("Kaster feil hvis noen prøver å endre navn til en person med vanlig fødselsnummer")
    fun endreForVanligStatsborger() {
        `when`(kjerneinfoService.hentKjerneinformasjon(Matchers.any())).thenReturn(mockKjerneinformasjonResponse(AREMARK_FNR))

        Assertions.assertThrows(ForbiddenException::class.java, { controller.endreNavn(AREMARK_FNR, lagRequest(AREMARK_FNR)) })
    }

    @Test
    @DisplayName("Kan endre navn til person med vanlig fødselsnummer, men som er utvandret")
    fun endreForUtvandretBorger() {
        val kjerneinformasjon = mockKjerneinformasjonResponse(AREMARK_FNR)
        kjerneinformasjon.person.personfakta.bostatus = Kodeverdi("UTVA", "UTVA")
        `when`(kjerneinfoService.hentKjerneinformasjon(Matchers.any())).thenReturn(kjerneinformasjon)
        val argumentCaptor = ArgumentCaptor.forClass(WSEndreNavnRequest::class.java)

        controller.endreNavn(AREMARK_FNR, lagRequest(AREMARK_FNR))

        verify(behandlePersonService, times(1)).endreNavn(argumentCaptor.capture())
    }

    @Test
    @DisplayName("Kaster feil om saksbehandler ikke har riktig rolle")
    fun sjekkerRolle() {
        `when`(kjerneinfoService.hentKjerneinformasjon(Matchers.any())).thenReturn(mockKjerneinformasjonResponse(D_NUMMER))
        `when`(ldapService.saksbehandlerHarRolle(INNLOGGET_SAKSBEHANDLER, ENDRE_NAVN_ROLLE)).thenReturn(false)

        val feilmelding = Assertions.assertThrows(ForbiddenException::class.java, { controller.endreNavn(AREMARK_FNR, lagRequest(D_NUMMER)) })

        assertTrue(feilmelding.message!!.contains(ENDRE_NAVN_ROLLE), "Feilmelding skal inneholde påkrevd rolle")
    }

    private fun lagRequest(D_NUMMER: String): EndreNavnRequest {
        val endreNavnRequest = EndreNavnRequest()
        endreNavnRequest.fornavn = FORNAVN
        endreNavnRequest.mellomnavn = MELLOMNAVN
        endreNavnRequest.etternavn = ETTERNAVN
        endreNavnRequest.fødselsnummer = D_NUMMER
        return endreNavnRequest
    }

    private fun mockKjerneinformasjonResponse(fødselsnummer: String): HentKjerneinformasjonResponse {
        val hentKjerneinformasjonResponse = HentKjerneinformasjonResponse()
        hentKjerneinformasjonResponse.person = Person()
        hentKjerneinformasjonResponse.person.fodselsnummer = Fodselsnummer(fødselsnummer)
        hentKjerneinformasjonResponse.person.personfakta = Personfakta()
        hentKjerneinformasjonResponse.person.personfakta.bostatus = Kodeverdi()
        return hentKjerneinformasjonResponse
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() = enableFeature(Feature.PERSON_REST_API)

        @AfterAll
        @JvmStatic
        fun afterAll() = disableFeature(Feature.PERSON_REST_API)
    }
}