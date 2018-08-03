package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppfolging

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsenhetService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.OppfolgingsinfoServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.feil.WSSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.ws.rs.NotAuthorizedException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val FNR = "10108000398"
private const val SAKSBEH_FORNAVN = "TEST"
private const val SAKSBEH_ETTERNAVN = "TESTER"
private const val SAKSBEH_IDENT = "Z000001"
private const val ENHET_ID = "N1234"
private const val ENHET_NAVN = "NAV AREMARK"

internal class OppfolgingControllerTest {

    private val unleashService: UnleashService = mock()
    private val oppfolgingsInfoV1: OppfolgingsinfoV1 = mock()
    private val ldapService: LDAPService = mock()
    private val aktoerPortType: AktoerPortType = mock()
    private val oppfolgingsEnhetService: OppfolgingsenhetService = mock()

    private val service = OppfolgingsinfoServiceImpl(oppfolgingsInfoV1, ldapService, aktoerPortType, oppfolgingsEnhetService)

    private val controller = OppfolgingController(service, unleashService)

    @BeforeEach
    fun before() {
        whenever(unleashService.isEnabled(Feature.NYTT_VISITTKORT)).thenReturn(true)
        whenever(aktoerPortType.hentAktoerIdForIdent(any())).thenReturn(mockAktoerResponse())
        whenever(ldapService.hentSaksbehandler(any())).thenReturn(mockSaksbehandlerResponse())
        whenever(oppfolgingsEnhetService.hentOppfolgingsenhet(any())).thenReturn(mockOppfølgingsEnhetResponse())
    }

    @Test
    fun`Kaster Auth exception`() {
        whenever(oppfolgingsInfoV1.hentOppfolgingsstatus(any())).thenReturn(unauthorizedMockResponse())

        assertFailsWith<NotAuthorizedException> { controller.hent(FNR) }
    }

    @Test
    fun`Tester at data settes`() {
        whenever(oppfolgingsInfoV1.hentOppfolgingsstatus(any())).thenReturn(mockOppfølgingsResponse())

        val response = controller.hent(FNR)

        assertEquals(true, response.get("erUnderOppfølging"))
    }

    private fun unauthorizedMockResponse() = OppfolgingsstatusResponse()
            .withWsSikkerhetsbegrensning(WSSikkerhetsbegrensning())

    private fun mockOppfølgingsResponse() = OppfolgingsstatusResponse()
            .withWsOppfolgingsdata(WSOppfolgingsdata()
                    .withAktorId(FNR)
                    .withErUnderOppfolging(true))

    private fun mockAktoerResponse() = HentAktoerIdForIdentResponse(FNR)

    private fun mockSaksbehandlerResponse() = Saksbehandler(SAKSBEH_FORNAVN, SAKSBEH_ETTERNAVN, SAKSBEH_IDENT)

    private fun mockOppfølgingsEnhetResponse() = Optional.of(AnsattEnhet(ENHET_ID, ENHET_NAVN))

}