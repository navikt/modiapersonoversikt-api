package no.nav.modiapersonoversikt.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.api.domain.Saksbehandler;
import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.modiapersonoversikt.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OppfolgingsinfoServiceImplTest {

    private static final boolean ER_UNDER_OPPFOLGING = true;
    private static final String OPPFOELGINGSENHET_ID = "0118";
    private static final String OPPFOELGINGSENHET_NAVN = "NAV Aremark";
    private static final String VEILEDER_IDENT = "z151444";
    private static final String VEILEDER_FORNAVN = "John";
    private static final String VEILEDER_ETTERNAVN = "Lennon";
    private static final String FODSELSNUMMER = "10108000398";
    private OppfolgingsinfoV1 oppfolgingsinfoV1Mock;
    private LDAPService ldapServiceMock;
    private OppfolgingsinfoApiService oppfolgingsinfoServiceMock;

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
        oppfolgingsinfoServiceMock = mockOppfolgingsinfoService();
    }

    private OppfolgingsinfoApiService mockOppfolgingsinfoService() {
        OppfolgingsinfoApiService mock = mock(OppfolgingsinfoApiService.class);
        when(mock.hentOppfolgingsinfo(anyString(), any())).thenReturn(new Oppfolgingsinfo( ER_UNDER_OPPFOLGING )
                .withVeileder(new Saksbehandler(VEILEDER_FORNAVN, VEILEDER_ETTERNAVN, VEILEDER_IDENT ))
                .withOppfolgingsenhet(new AnsattEnhet(OPPFOELGINGSENHET_ID, OPPFOELGINGSENHET_NAVN )));
        return mock;
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

    @Test
    @DisplayName("Henter ut oppfølgingsflagget til bruker fra veilarboppfolging")
    void henterErUnderOppfolgingFraTjenesten() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        assertEquals(ER_UNDER_OPPFOLGING, oppfolgingsinfo.erUnderOppfolging);
    }

    @Test
    @DisplayName("Henter veileders navn fra veilarboppfolging")
    void henterVeiledernavnFraTjenesten() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        assertThat(oppfolgingsinfo.getVeileder().get().etternavn, is(VEILEDER_ETTERNAVN));
        assertThat(oppfolgingsinfo.getVeileder().get().fornavn, is(VEILEDER_FORNAVN));
    }

    @Test
    @DisplayName("Returnerer oppfølgingsenhet-id")
    void returnererOppfolgingsenhetId() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        assertThat(oppfolgingsinfo.getOppfolgingsenhet().get().enhetId, is(OPPFOELGINGSENHET_ID));
    }


    @Test
    @DisplayName("Returnerer oppfølgingsenhetnavn")
    void returnererOppfolgingsenhetNavn() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        assertThat(oppfolgingsinfo.getOppfolgingsenhet().get().enhetNavn, is(OPPFOELGINGSENHET_NAVN));
    }


    @Disabled
    @Test
    @DisplayName("Kaller LDAP med riktig veilederident")
    void kallerLdapMedRiktigVeilederident() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        verify(ldapServiceMock).hentSaksbehandler(stringArgumentCaptor.capture());
        System.out.println(ldapServiceMock.hentSaksbehandler(stringArgumentCaptor.capture()));
        assertThat(stringArgumentCaptor.getValue(), is(VEILEDER_IDENT));
    }

    @Test
    @DisplayName("Kaller ikke LDAP hvis bruker ikke er under oppfolging")
    void kallerIkkeLDAPHvisIkkeUnderOppfolging() {
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse().withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(false)));

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        verify(ldapServiceMock, never()).hentSaksbehandler(anyString());
    }

    @Test
    @DisplayName("Kaller ikke LDAP hvis bruker ikke har veileder")
    void kallerIkkeLDAPHvisBrukerIkkeHarVeileder() {
        when(oppfolgingsinfoV1Mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse().withWsOppfolgingsdata(new WSOppfolgingsdata().withErUnderOppfolging(true)));

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER, ldapServiceMock);

        verify(ldapServiceMock, never()).hentSaksbehandler(anyString());
    }
}
