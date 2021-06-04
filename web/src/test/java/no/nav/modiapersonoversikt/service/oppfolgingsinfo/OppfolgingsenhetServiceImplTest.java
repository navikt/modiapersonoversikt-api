package no.nav.modiapersonoversikt.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.api.service.oppfolgingsinfo.OppfolgingsenhetService;
import no.nav.modiapersonoversikt.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.HentOppfoelgingsstatusUgyldigInput;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingsstatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class OppfolgingsenhetServiceImplTest {

    private static final String OPPFOELGINGSENHET_ID = "0118";
    private static final String OPPFOELGINGSENHET_NAVN = "NAV Aremark";
    private static final String FODSELSNUMMER = "10108000398";
    private OppfoelgingPortType oppfoelgingPortTypeMock;
    private OrganisasjonEnhetV2Service organisasjonEnhetV2ServiceMock;
    private OppfolgingsenhetService oppfolgingsenhetServiceMock;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;


    @BeforeEach
    void beforeEach() {
        setupMocks();
        stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    }

    private void setupMocks() {
        oppfoelgingPortTypeMock = mockOppfoelgingPortType();
        organisasjonEnhetV2ServiceMock = mockOrganisasjonEnhetV2Service();
        oppfolgingsenhetServiceMock = new OppfolgingsenhetServiceImpl(oppfoelgingPortTypeMock, organisasjonEnhetV2ServiceMock);
    }

    private OppfoelgingPortType mockOppfoelgingPortType() {
        OppfoelgingPortType oppfoelgingPortType = mock(OppfoelgingPortType.class);
        WSHentOppfoelgingsstatusResponse response = new WSHentOppfoelgingsstatusResponse();
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
    @DisplayName("Kaller Arena med riktig fødselsnummer")
    void kallerArenaMedRiktigFodselsnummer() throws Exception {
        ArgumentCaptor<WSHentOppfoelgingsstatusRequest> argumentCaptor = ArgumentCaptor.forClass(WSHentOppfoelgingsstatusRequest.class);

        AnsattEnhet oppfolgingsinfo = oppfolgingsenhetServiceMock.hentOppfolgingsenhet(FODSELSNUMMER).get();

        verify(oppfoelgingPortTypeMock).hentOppfoelgingsstatus(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getPersonidentifikator(), is(FODSELSNUMMER));
    }

    @Test
    @DisplayName("Kaller NORG med riktig enhetsid")
    void kallerNorgMedRiktigEnhetsid() {
        ArgumentCaptor<OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter> argumentCaptor =
                ArgumentCaptor.forClass(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.class);

        AnsattEnhet oppfolgingsinfo = oppfolgingsenhetServiceMock.hentOppfolgingsenhet(FODSELSNUMMER).get();

        verify(organisasjonEnhetV2ServiceMock).hentEnhetGittEnhetId(stringArgumentCaptor.capture(), argumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(OPPFOELGINGSENHET_ID));
    }

}
