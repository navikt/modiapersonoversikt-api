package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusUgyldigInput;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.HentOppfoelgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private OppfoelgingPortType oppfoelgingPortTypeMock;
    private OrganisasjonEnhetV2Service organisasjonEnhetV2ServiceMock;
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
        oppfoelgingPortTypeMock = mockOppfoelgingPortType();
        organisasjonEnhetV2ServiceMock = mockOrganisasjonEnhetV2Service();
        oppfolgingsinfoServiceMock = new OppfolgingsinfoServiceImpl(oppfolgingsinfoV1Mock, ldapServiceMock, aktoerPortTypeMock,
                oppfoelgingPortTypeMock, organisasjonEnhetV2ServiceMock);
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

    private OppfoelgingPortType mockOppfoelgingPortType() {
        OppfoelgingPortType oppfoelgingPortType = mock(OppfoelgingPortType.class);
        HentOppfoelgingsstatusResponse response = new HentOppfoelgingsstatusResponse();
        response.setNavOppfoelgingsenhet(OPPFOELGINGSENHET_ID);
        try {
            when(oppfoelgingPortType.hentOppfoelgingsstatus(any())).thenReturn(response);
        } catch (HentOppfoelgingsstatusPersonIkkeFunnet | HentOppfoelgingsstatusSikkerhetsbegrensning | HentOppfoelgingsstatusUgyldigInput hentOppfoelgingsstatusPersonIkkeFunnet) {
            hentOppfoelgingsstatusPersonIkkeFunnet.printStackTrace();
        }
        return oppfoelgingPortType;
    }

    private OrganisasjonEnhetV2Service mockOrganisasjonEnhetV2Service() {
        OrganisasjonEnhetV2Service organisasjonEnhetV2Service = mock(OrganisasjonEnhetV2Service.class);
        when(organisasjonEnhetV2Service.hentEnhetGittEnhetId(anyString(), any()))
                .thenReturn(Optional.of(new AnsattEnhet(OPPFOELGINGSENHET_ID, OPPFOELGINGSENHET_NAVN)));
        return organisasjonEnhetV2Service;
    }

    @Test
    @DisplayName("Henter ut oppfølgingsflagget til bruker fra veilarboppfolging")
    void henterErUnderOppfolgingFraTjenesten() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        assertEquals(ER_UNDER_OPPFOLGING, oppfolgingsinfo.erUnderOppfolging);
    }

    @Test
    @DisplayName("Henter veileders navn fra veilarboppfolging")
    void henterVeiledernavnFraTjenesten() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        assertThat(oppfolgingsinfo.getSaksbehandler().get().etternavn, is(VEILEDER_ETTERNAVN));
        assertThat(oppfolgingsinfo.getSaksbehandler().get().fornavn, is(VEILEDER_FORNAVN));
    }

    @Test
    @DisplayName("Returnerer oppfølgingsenhet-id")
    void returnererOppfolgingsenhetId() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        assertThat(oppfolgingsinfo.getSaksbehandlerenhet().get().enhetId, is(OPPFOELGINGSENHET_ID));
    }

    @Test
    @DisplayName("Returnerer oppfølgingsenhetnavn")
    void returnererOppfolgingsenhetNavn() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        assertThat(oppfolgingsinfo.getSaksbehandlerenhet().get().enhetNavn, is(OPPFOELGINGSENHET_NAVN));
    }

    @Test
    @DisplayName("Kaller AktoerPortType med riktig fodselsnummer")
    void kallerAktoerPortTypeMedRiktigFnr() throws HentAktoerIdForIdentPersonIkkeFunnet {
        ArgumentCaptor<HentAktoerIdForIdentRequest> argumentCaptor = ArgumentCaptor.forClass(HentAktoerIdForIdentRequest.class);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        verify(aktoerPortTypeMock).hentAktoerIdForIdent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getIdent(), is(FODSELSNUMMER));
    }

    @Test
    @DisplayName("Kaller Oppfolgingsinfo med riktig aktør-id")
    void kallerOppfolgingsinfoMedRiktigAktoerId() {
        ArgumentCaptor<OppfolgingsstatusRequest> argumentCaptor = ArgumentCaptor.forClass(OppfolgingsstatusRequest.class);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        verify(oppfolgingsinfoV1Mock).hentOppfolgingsstatus(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAktorId(), is(AKTOERID));
    }

    @Test
    @DisplayName("Kaller LDAP med riktig veilederident")
    void kallerLdapMedRiktigVeilederident() {
        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        verify(ldapServiceMock).hentSaksbehandler(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(VEILEDER_IDENT));
    }

    @Test
    @DisplayName("Kaller Arena med riktig fødselsnummer")
    void kallerArenaMedRiktigFodselsnummer() throws Exception {
        ArgumentCaptor<HentOppfoelgingsstatusRequest> argumentCaptor = ArgumentCaptor.forClass(HentOppfoelgingsstatusRequest.class);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        verify(oppfoelgingPortTypeMock).hentOppfoelgingsstatus(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getPersonidentifikator(), is(FODSELSNUMMER));
    }

    @Test
    @DisplayName("Kaller NORG med riktig enhetsid")
    void kallerNorgMedRiktigEnhetsid() {
        ArgumentCaptor<OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter> argumentCaptor =
                ArgumentCaptor.forClass(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.class);

        Oppfolgingsinfo oppfolgingsinfo = oppfolgingsinfoServiceMock.hentOppfolgingsinfo(FODSELSNUMMER).get();

        verify(organisasjonEnhetV2ServiceMock).hentEnhetGittEnhetId(stringArgumentCaptor.capture(), argumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(OPPFOELGINGSENHET_ID));
    }

}