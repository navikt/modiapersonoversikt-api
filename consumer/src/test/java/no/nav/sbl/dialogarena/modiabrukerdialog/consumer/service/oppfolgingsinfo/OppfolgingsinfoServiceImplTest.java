package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.feil.WSSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OppfolgingsinfoServiceImplTest {

    private static final boolean ER_UNDER_OPPFOLGING = true;
    private static final String AKTOERID = "***REMOVED***";
    private static final String OPPFOELGINGSENHET_ID = "0118";
    private static final String OPPFOELGINGSENHET_NAVN = "NAV Aremark";
    private static final String VEILEDER_IDENT = "z151444";
    private static final String VEILEDER_FORNAVN = "John";
    private static final String VEILEDER_ETTERNAVN = "Lennon";
    private static final String FODSELSNUMMER = "10108000398";
    private OppfolgingsinfoV1 oppfolgingsinfoV1Mock;
    private LDAPService ldapServiceMock;
    private AktoerPortType aktoerPortTypeMock;
    private OppfolgingsenhetService oppfolgingsenhetServiceMock;
    private OppfolgingsinfoService oppfolgingsinfoServiceMock;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        setupMocks();
        stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    }

    private void setupMocks() {
        oppfolgingsinfoV1Mock = mockOppfolginsinfoV1();
        ldapServiceMock = mockLdapService();
        aktoerPortTypeMock = mockAktoerPortType();
        oppfolgingsenhetServiceMock = mockOppfolgingsenhetService();
        oppfolgingsinfoServiceMock = new OppfolgingsinfoServiceImpl(oppfolgingsinfoV1Mock, ldapServiceMock, aktoerPortTypeMock,
                oppfolgingsenhetServiceMock);
    }

    private OppfolgingsenhetService mockOppfolgingsenhetService() {
        OppfolgingsenhetService oppfolgingsenhetServiceMock = mock(OppfolgingsenhetService.class);
        when(oppfolgingsenhetServiceMock.hentOppfolgingsenhet(anyString()))
                .thenReturn(Optional.of(new AnsattEnhet(OPPFOELGINGSENHET_ID, OPPFOELGINGSENHET_NAVN)));
        return oppfolgingsenhetServiceMock;
    }

    private OppfolgingsinfoV1 mockOppfolginsinfoV1() {
        OppfolgingsinfoV1 oppfolgingsinfoV1Mock = mock(OppfolgingsinfoV1.class);
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any())).thenReturn(new OppfolgingsstatusResponse()
                .withWsOppfolgingsdata(new WSOppfolgingsdata()
                        .withErUnderOppfolging(ER_UNDER_OPPFOLGING)
                        .withVeilederIdent(VEILEDER_IDENT)));
        return oppfolgingsinfoV1Mock;
    }

    private LDAPService mockLdapService() {
        LDAPService ldapService = mock(LDAPService.class);
        when(ldapService.hentSaksbehandler(anyString())).thenReturn(new Saksbehandler(VEILEDER_FORNAVN, VEILEDER_ETTERNAVN, VEILEDER_IDENT));
        return ldapService;
    }

    private AktoerPortType mockAktoerPortType() {
        AktoerPortType aktoerPortType = mock(AktoerPortType.class);
        try {
            when(aktoerPortType.hentAktoerIdForIdent(any())).thenReturn(new HentAktoerIdForIdentResponse(AKTOERID));
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            hentAktoerIdForIdentPersonIkkeFunnet.printStackTrace();
        }
        return aktoerPortType;
    }

    @Test
    @DisplayName("Henter ut oppfølgingsflagget til bruker fra veilarboppfolging")
    void henterErUnderOppfolgingFraTjenesten() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        assertEquals(ER_UNDER_OPPFOLGING, oppfolgingsinfo.erUnderOppfolging);
    }

    @Test
    @DisplayName("Henter veileders navn fra veilarboppfolging")
    void henterVeiledernavnFraTjenesten() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        assertThat(oppfolgingsinfo.getVeileder().get().etternavn, is(VEILEDER_ETTERNAVN));
        assertThat(oppfolgingsinfo.getVeileder().get().fornavn, is(VEILEDER_FORNAVN));
    }

    @Test
    @DisplayName("Returnerer oppfølgingsenhet-id")
    void returnererOppfolgingsenhetId() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        assertThat(oppfolgingsinfo.getOppfolgingsenhet().get().enhetId, is(OPPFOELGINGSENHET_ID));
    }

    @Test
    @DisplayName("Returnerer oppfølgingsenhetnavn")
    void returnererOppfolgingsenhetNavn() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        assertThat(oppfolgingsinfo.getOppfolgingsenhet().get().enhetNavn, is(OPPFOELGINGSENHET_NAVN));
    }

    @Test
    @DisplayName("Kaller AktoerPortType med riktig fodselsnummer")
    void kallerAktoerPortTypeMedRiktigFnr() throws HentAktoerIdForIdentPersonIkkeFunnet {
        ArgumentCaptor<HentAktoerIdForIdentRequest> argumentCaptor = ArgumentCaptor.forClass(HentAktoerIdForIdentRequest.class);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        verify(aktoerPortTypeMock).hentAktoerIdForIdent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getIdent(), is(FODSELSNUMMER));
    }

    @Test
    @DisplayName("Kaller Oppfolgingsinfo med riktig aktør-id")
    void kallerOppfolgingsinfoMedRiktigAktoerId() {
        ArgumentCaptor<OppfolgingsstatusRequest> argumentCaptor = ArgumentCaptor.forClass(OppfolgingsstatusRequest.class);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        verify(oppfolgingsinfoV1Mock).hentOppfolgingsstatus(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAktorId(), is(AKTOERID));
    }

    @Test
    @DisplayName("Kaller LDAP med riktig veilederident")
    void kallerLdapMedRiktigVeilederident() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        verify(ldapServiceMock).hentSaksbehandler(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(VEILEDER_IDENT));
    }

    @Test
    @DisplayName("Kaller OppfolgingsenhetService med riktig fødselsnummer")
    void kallerOppfolgingsenhetServiceMedRiktigFodselsnummer() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        verify(oppfolgingsenhetServiceMock).hentOppfolgingsenhet(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(FODSELSNUMMER));
    }

    @Test
    @DisplayName("Kaller ikke LDAP hvis bruker ikke er under oppfolging")
    void kallerIkkeLDAPHvisIkkeUnderOppfolging() {
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse().withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(false)));

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        verify(ldapServiceMock, never()).hentSaksbehandler(anyString());
    }

    @Test
    @DisplayName("Kaller ikke LDAP hvis bruker ikke har veileder")
    void kallerIkkeLDAPHvisBrukerIkkeHarVeileder() {
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse().withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(true)));

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER);

        verify(ldapServiceMock, never()).hentSaksbehandler(anyString());
    }

    @Test
    @DisplayName("Kaster exception hvis Oppfolgingsinfo returnerer tom respons")
    void kasterExceptionHvisOppfolgingsinfoReturnererTomResponse() {
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse());

        assertThrows(RuntimeException.class, () -> oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER));
    }

    @Test
    @DisplayName("Kaster exception hvis Oppfolgingsinfo returnerer WSSikkerhetsbegrensning")
    void kasterExceptionHvisOppfolgingsinfoReturnererSikkerhetsbegrensning() {
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse().withWsSikkerhetsbegrensning(new WSSikkerhetsbegrensning()));

        assertThrows(RuntimeException.class, () -> oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER));
    }

}