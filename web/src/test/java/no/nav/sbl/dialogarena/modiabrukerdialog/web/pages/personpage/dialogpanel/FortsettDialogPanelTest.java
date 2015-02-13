package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;


import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.*;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.TestUtils.createMockSaker;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
public class FortsettDialogPanelTest extends WicketPageTest {

    private static final String FNR = "fnr";
    private static final String FORNAVN = "fornavn";
    private static final String SPORSMAL_ID = "id";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = Temagruppe.FMLI.name();
    private static final String VALGT_ENHET = "valgtEnhet";
    private static final Answer<Melding> RETURNER_SAMME_MELDING = new Answer<Melding>() {
        @Override
        public Melding answer(InvocationOnMock invocation) throws Throwable {
            Melding melding = ((Melding) invocation.getArguments()[0]);
            melding.traadId = "traadId";
            return melding;
        }
    };

    @Captor
    private ArgumentCaptor<Melding> meldingArgumentCaptor;

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
        grunnInfo = new GrunnInfo(FNR, FORNAVN);
        saker = createMockSaker();
        when(sakerService.hentSaker(anyString())).thenReturn(saker);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);

        testFortsettDialogPanel = new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmal()), Optional.<String>none());
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
    public void senderHenvendelseMedRiktigeFelterTilHenvendelseUtsendingService() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
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
    public void senderSvarDersomManVelgerTekstSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_SKRIFTLIG));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferatDersomManVelgerTelefonSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 1)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_TELEFON));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferatDersomManVelgerOppmoteSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 2)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderSvarDersomManVelgerBrukerKanSvareMenHarValgtReferatSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 2)
                .check("fortsettdialogformelementer:brukerKanSvare", true)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderIkkeSporsmalOgFaarFeilmeldingDersomManVelgerBrukerKanSvareOgSkriftligKanalMenIkkeVelgerJournalforingssak() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .check("fortsettdialogformelementer:brukerKanSvare", true)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().and(withId("feedback")));

        verify(henvendelseUtsendingService, never()).sendHenvendelse(any(Melding.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderOgJournalforerSporsmalDersomManVelgerBrukerKanSvareOgSkriftligKanalOgJournalforingssak() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        when(henvendelseUtsendingService.sendHenvendelse(any(Melding.class), any(Optional.class))).then(RETURNER_SAMME_MELDING);
        testFortsettDialogPanel.getModelObject().valgtSak = saker.getSakerListeFagsak().get(0).saksliste.get(0);

        wicket.goToPageWith(testFortsettDialogPanel)
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 0)
                .check("fortsettdialogformelementer:brukerKanSvare", true)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SPORSMAL_MODIA_UTGAAENDE));
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
    public void viserTraadToggleLenkeHvisSvarFinnes() {
        wicket.goToPageWith(new FortsettDialogPanel(SPORSMAL_ID, grunnInfo, asList(lagSporsmal(), lagSvar()), Optional.<String>none()))
                .should().containComponent(thatIsVisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void viserIkkeTraadToggleLenkeHvisIngenSvarFinnes() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(thatIsInvisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void togglerVisningAvTraad() {
        wicket.goToPageWith(new FortsettDialogPanel(SPORSMAL_ID, grunnInfo, asList(lagSporsmal(), lagSvar()), Optional.<String>none()))
                .should().containComponent(thatIsInvisible().and(withId("traadcontainer")))
                .onComponent(withId("vistraadcontainer")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(withId("traadcontainer")));
    }

    @Test
    public void viserLeggTilbakePanel() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsVisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void leggTilbakeLenkeHarTekstenLeggTilbake() {
        wicket.goToPageWith(testFortsettDialogPanel);

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("fortsettdialogpanel.avbryt.leggtilbake");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    @Test
    public void leggTilbakeLenkeHarTekstenAvbryt() {
        wicket.goToPageWith(testFortsettDialogPanel);

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("fortsettdialogpanel.avbryt.avbryt");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    @Test
    public void skalViseFornavnISubmitKnapp() {
        when(cmsContentRetriever.hentTekst(anyString())).thenReturn("Tekst fra mock-cms %s");

        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmal()), Optional.<String>none()))
                .should().containPatterns(FORNAVN);
    }

    private Melding lagSporsmal() {
        Melding sporsmal = new Melding().withId(SPORSMAL_ID).withOpprettetDato(now());
        sporsmal.temagruppe = TEMAGRUPPE;
        return sporsmal;
    }

    private Melding lagSvar() {
        return new Melding().withOpprettetDato(now()).withType(SVAR_SKRIFTLIG).withFritekst("fritekst").withTemagruppe(TEMAGRUPPE);
    }

}