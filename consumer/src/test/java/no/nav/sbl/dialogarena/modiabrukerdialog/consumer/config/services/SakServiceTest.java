package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgave.v2.HentOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSFerdigstillOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV2PortTypeMock.lagWSOppgave;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HenvendelseTestConfig.class, OppgaveTestConfig.class})
public class SakServiceTest {

    private final static String FNR = "fnr";
    private final static String SPORSMAL_ID = "id";
    private final static String FRITEKST = "fritekst";
    private final static String TEMAGRUPPE = "temagruppe";

    @Inject
    private SakService sakService;
    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected SendHenvendelsePortType sendHenvendelsePortType;
    @Inject
    private Oppgave oppgaveWS;
    @Inject
    private Oppgavebehandling oppgavebehandlingWS;

    @Captor
    ArgumentCaptor<WSSendHenvendelseRequest> wsSendHenvendelseRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentOppgaveRequest> hentOppgaveRequestCaptor;
    @Captor
    ArgumentCaptor<WSFinnOppgaveListeRequest> finnOppgaveListeRequestCaptor;
    @Captor
    ArgumentCaptor<WSFerdigstillOppgaveBolkRequest> ferdigstillOppgaveBolkRequestCaptor;
    @Captor
    ArgumentCaptor<WSLagreOppgaveRequest> lagreOppgaveRequestCaptor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void skalHenteSporsmaalOgTilordneIGsak() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOppgaveIkkeFunnet {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(mockWSHentHenvendelseResponse());
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());

        Sporsmal sporsmal = sakService.getSporsmalOgTilordneIGsak(SPORSMAL_ID);

        verify(oppgavebehandlingWS).lagreOppgave(lagreOppgaveRequestCaptor.capture());
        assertThat(lagreOppgaveRequestCaptor.getValue().getEndreOppgave().getAnsvarligId(), is(SubjectHandler.getSubjectHandler().getUid()));

        assertThat(sporsmal.id, is(SPORSMAL_ID));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
    }

    @Test
    public void skalSendeReferat() {
        sakService.sendReferat(new Referat().withFnr(FNR).withFritekst(FRITEKST));

        verify(sendHenvendelsePortType).sendHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(REFERAT.name()));
    }

    @Test
    public void skalSendeSvar() {
        sakService.sendSvar(new Svar().withFnr(FNR).withFritekst(FRITEKST));

        verify(sendHenvendelsePortType).sendHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(SVAR.name()));
    }

    @Test
    public void skalHenteOppgavefraGsak() throws HentOppgaveOppgaveIkkeFunnet {
        when(oppgaveWS.hentOppgave(any(WSHentOppgaveRequest.class))).thenReturn(mockHentOppgaveResponse());

        sakService.hentOppgaveFraGsak("1");
        verify(oppgaveWS).hentOppgave(hentOppgaveRequestCaptor.capture());
        assertThat(hentOppgaveRequestCaptor.getValue().getOppgaveId(), is("1"));
    }

    @Test
    public void skalPlukkeOppgaveFraGsak() {
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        WSFinnOppgaveListeResponse finnOppgaveListeResponse = new WSFinnOppgaveListeResponse();
        finnOppgaveListeResponse.getOppgaveListe().add(lagWSOppgave());
        when(oppgaveWS.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class))).thenReturn(finnOppgaveListeResponse);

        sakService.plukkOppgaveFraGsak("HJELPEMIDLER");
        verify(oppgaveWS).finnOppgaveListe(finnOppgaveListeRequestCaptor.capture());
        assertThat(finnOppgaveListeRequestCaptor.getValue().getSok().getFagomradeKodeListe().get(0), is("HJE"));
        assertThat(finnOppgaveListeRequestCaptor.getValue().getFilter().getMaxAntallSvar(), is(1));
        assertThat(finnOppgaveListeRequestCaptor.getValue().getFilter().isUfordelte(), is(true));
    }

    @Test
    public void skalFerdigstilleOppgaveFraGsak() {
        sakService.ferdigstillOppgaveFraGsak("1");
        verify(oppgavebehandlingWS).ferdigstillOppgaveBolk(ferdigstillOppgaveBolkRequestCaptor.capture());
        assertThat(ferdigstillOppgaveBolkRequestCaptor.getValue().getOppgaveIdListe().get(0), is("1"));
    }

    @Test
    public void skalHenteSporsmaalFraOppgaveId() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXmlSporsmaal("id1", "fritekst1"),
                        createXmlSporsmaal("id2", "fritekst2"),
                        createXmlSporsmaal("id3", "fritekst3")
                );

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        Sporsmal sporsmal = sakService.getSporsmalFromOppgaveId("fnr", "id2");

        assertThat(sporsmal.fritekst, is("fritekst2"));
    }

    private WSHentHenvendelseResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseResponse().withAny(
                new XMLBehandlingsinformasjon()
                        .withBehandlingsId(SPORSMAL_ID)
                        .withOpprettetDato(DateTime.now())
                        .withHenvendelseType(SPORSMAL.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLSporsmal().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        );
    }

    private WSHentOppgaveResponse mockHentOppgaveResponse() {
        return new WSHentOppgaveResponse().withOppgave(lagWSOppgave());
    }

    private XMLBehandlingsinformasjon createXmlSporsmaal(String oppgaveId, String fritekst) {
        XMLBehandlingsinformasjon xmlBehandlingsinformasjon = new XMLBehandlingsinformasjon();
        xmlBehandlingsinformasjon.setMetadataListe(new XMLMetadataListe().withMetadata(new XMLSporsmal().withOppgaveIdGsak(oppgaveId).withFritekst(fritekst)));
        return xmlBehandlingsinformasjon;
    }
}
