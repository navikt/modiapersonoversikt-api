package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;


import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.DialogPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning.ENHET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning.SAKSBEHANDLER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.TestUtils.createMockSaker;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel.erTilknyttetAnsatt;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DialogPanelMockContext.class})
public class FortsettDialogPanelTest extends WicketPageTest {

    private static final String FNR = "fnr";
    private static final String FORNAVN = "Fornavn";
    private static final String SPORSMAL_ID = "id";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = Temagruppe.FMLI.name();
    private static final String VALGT_ENHET = "valgtEnhet";

    @Captor
    private ArgumentCaptor<Melding> meldingArgumentCaptor;
    @Captor
    private ArgumentCaptor<Optional<Sak>> sakArgumentCaptor;
    @Captor
    private ArgumentCaptor<Optional<String>> optionalStringArgumentCaptor;

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private CmsContentRetriever cmsContentRetriever;
    @Inject
    private SakerService sakerService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private FortsettDialogPanel testFortsettDialogPanel;

    private Saker saker;
    private GrunnInfo grunnInfo;

    @Before
    public void setUp() {
        grunnInfo = new GrunnInfo(new Bruker(FNR, FORNAVN, "", ""), new Saksbehandler("", "", ""));
        saker = createMockSaker();
        when(sakerService.hentSaker(anyString())).thenReturn(saker);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);

        testFortsettDialogPanel = new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraBruker()), Optional.<String>none());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(withId("temagruppe").and(ofType(Label.class)))
                .should().containComponent(withId("sporsmal").and(ofType(TidligereMeldingPanel.class)))
                .should().containComponent(withId("svarliste").and(ofType(ListView.class)))
                .should().containComponent(withId("dato").and(ofType(Label.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("kanalbeskrivelse").and(ofType(Label.class)))
                .should().containComponent(withId("fortsettdialogform"))
                .should().containComponent(withId("leggtilbakepanel").and(ofType(LeggTilbakePanel.class)))
                .should().containComponent(withId("leggtilbake").and(ofType(AjaxLink.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderHenvendelseMedRiktigeFelterTilHenvendelseUtsendingService() throws Exception {
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.traadId, is(SPORSMAL_ID));
        assertThat(melding.temagruppe, is(TEMAGRUPPE));
        assertThat(melding.kanal, is(Kanal.TEKST.name()));
        assertThat(melding.fritekst, is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderSvarDersomManVelgerTekstSomKanal() throws Exception {
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_SKRIFTLIG));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferatDersomManVelgerTelefonSomKanal() throws Exception {
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 1)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_TELEFON));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferatDersomManVelgerOppmoteSomKanal() throws Exception {
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 2)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderSvarDersomManVelgerBrukerKanSvareMenHarValgtReferatSomKanal() throws Exception {
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 2)
                .check("fortsettdialogformelementer:brukerKanSvare", true)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderIkkeSporsmalOgFaarFeilmeldingDersomManVelgerBrukerKanSvareOgSkriftligKanalMenIkkeVelgerJournalforingssak() throws Exception {
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .check("fortsettdialogformelementer:brukerKanSvare", true)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().and(withId("feedback")));

        verify(henvendelseUtsendingService, never()).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderOgJournalforerSporsmalDersomManVelgerBrukerKanSvareOgSkriftligKanalOgJournalforingssak() throws Exception {
        reset(henvendelseUtsendingService);

        Sak sak = saker.getSakerListeFagsak().get(0).saksliste.get(0);
        testFortsettDialogPanel.getModelObject().valgtSak = sak;

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .check("fortsettdialogformelementer:brukerKanSvare", true)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), sakArgumentCaptor.capture());

        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SPORSMAL_MODIA_UTGAAENDE));

        Sak sendtSak = sakArgumentCaptor.getValue().get();
        assertThat(sendtSak, is(sak));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderOppgaveIdTilFerdigstillelseDersomDenneErSatt() throws Exception {
        reset(henvendelseUtsendingService);

        String oppgaveId = "oppgaveid";
        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraBruker()), optional(oppgaveId)))
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 1)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), optionalStringArgumentCaptor.capture(), any(Optional.class));

        String sendtOppgaveId = optionalStringArgumentCaptor.getValue().get();
        assertThat(sendtOppgaveId, is(oppgaveId));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void disablerBrukerKanSvareDersomManHarValgtReferatSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 2)
                .andReturn()
                .onComponent(withId("kanal")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsDisabled().and(withId("brukerKanSvare")));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(withId("kanal").and(withModelObject(is(TEKST))));
    }

    @Test
    public void viserTemagruppenFraSporsmalet() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(withId("temagruppe").and(withTextSaying(testFortsettDialogPanel.getString(TEMAGRUPPE))));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("fortsettdialogform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void viserFeilmeldingDersomSendHenvendelseKasterOppgaveErFerdigstilt() throws Exception {
        doThrow(new HenvendelseUtsendingService.OppgaveErFerdigstilt())
                .when(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().and(ofType(KvitteringsPanel.class)))
                .should().containComponent(thatIsInvisible().and(withId("send")))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbake")))
                .should().containComponent(thatIsVisible().and(ofType(FeedbackPanel.class)));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(testFortsettDialogPanel.getString("fortsettdialogform.feilmelding.oppgaveferdigstilt")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void garTilKvitteringssideOgsaDersomJournalforingKasterException() throws Exception {
        doThrow(new JournalforingFeilet()).when(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().and(ofType(KvitteringsPanel.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void viserFeilmeldingDersomSendHenvendelseKasterException() throws Exception {
        doThrow(new Exception()).when(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().and(ofType(KvitteringsPanel.class)))
                .should().containComponent(thatIsVisible().and(withId("send")))
                .should().containComponent(thatIsVisible().and(withId("leggtilbake")))
                .should().containComponent(thatIsVisible().and(ofType(FeedbackPanel.class)));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(testFortsettDialogPanel.getString("dialogpanel.feilmelding.journalforing")));
    }

    @Test
    public void viserTraadToggleLenkeHvisSvarFinnes() {
        wicket.goToPageWith(new FortsettDialogPanel(SPORSMAL_ID, grunnInfo, asList(lagSporsmalFraBruker(), lagSvar()), Optional.<String>none()))
                .should().containComponent(thatIsVisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void viserIkkeTraadToggleLenkeHvisIngenSvarFinnes() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(thatIsInvisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void togglerVisningAvTraad() {
        wicket.goToPageWith(new FortsettDialogPanel(SPORSMAL_ID, grunnInfo, asList(lagSporsmalFraBruker(), lagSvar()), Optional.<String>none()))
                .should().containComponent(thatIsInvisible().and(withId("traadcontainer")))
                .onComponent(withId("vistraadcontainer")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(withId("traadcontainer")));
    }

    @Test
    public void viserLeggTilbakePanelDersomSporsmalErFraBruker() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsVisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void viserIkkeLeggTilbakeDersomSporsmalIkkeErEtSporsmalFraBruker() {
        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraNAV().withErTilknyttetAnsatt(true)), Optional.<String>none()))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void skalViseFornavnISubmitKnapp() {
        when(cmsContentRetriever.hentTekst(anyString())).thenReturn("Tekst fra mock-cms %s");

        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraBruker()), Optional.<String>none()))
                .should().containPatterns(FORNAVN);
    }

    @Test
    public void tilknyttetAnsattBlirSattTilSaksbehandlerForEnkeltstaaendeSporsmalFraBruker() {
        OppgaveTilknytning oppgaveTilknytning = erTilknyttetAnsatt(singletonList(lagSporsmalFraBruker()));

        assertThat(oppgaveTilknytning, is(SAKSBEHANDLER));
    }

    @Test
    public void tilknyttetAnsattArverEnhet() {
        Melding sporsmalFraBruker = lagSporsmalFraBruker();
        sporsmalFraBruker.opprettetDato = DateTime.now().minusDays(2);

        Melding sporsmalFraNAV = lagSporsmalFraNAV();
        sporsmalFraNAV.opprettetDato = DateTime.now();
        sporsmalFraNAV.erTilknyttetAnsatt = false;
        OppgaveTilknytning oppgaveTilknytning = erTilknyttetAnsatt(asList(sporsmalFraBruker, sporsmalFraNAV));

        assertThat(oppgaveTilknytning, is(ENHET));
    }

    @Test
    public void tilknyttetAnsattArverSaksbehandler() {
        Melding sporsmalFraBruker = lagSporsmalFraBruker();
        sporsmalFraBruker.opprettetDato = DateTime.now().minusDays(2);

        Melding sporsmalFraNAV = lagSporsmalFraNAV();
        sporsmalFraNAV.opprettetDato = DateTime.now();
        sporsmalFraNAV.erTilknyttetAnsatt = true;
        OppgaveTilknytning oppgaveTilknytning = erTilknyttetAnsatt(asList(sporsmalFraBruker, sporsmalFraNAV));

        assertThat(oppgaveTilknytning, is(SAKSBEHANDLER));
    }

    private Melding lagSporsmalFraBruker() {
        Melding sporsmal = new Melding()
                .withType(SPORSMAL_SKRIFTLIG)
                .withId(SPORSMAL_ID)
                .withOpprettetDato(now())
                .withErTilknyttetAnsatt(false);
        sporsmal.temagruppe = TEMAGRUPPE;
        return sporsmal;
    }

    private Melding lagSporsmalFraNAV() {
        Melding sporsmal = new Melding()
                .withType(Meldingstype.SPORSMAL_MODIA_UTGAAENDE)
                .withId(SPORSMAL_ID)
                .withOpprettetDato(now());
        sporsmal.temagruppe = TEMAGRUPPE;
        return sporsmal;
    }

    private Melding lagSvar() {
        return new Melding().withOpprettetDato(now()).withType(SVAR_SKRIFTLIG).withFritekst("fritekst").withTemagruppe(TEMAGRUPPE);
    }

}