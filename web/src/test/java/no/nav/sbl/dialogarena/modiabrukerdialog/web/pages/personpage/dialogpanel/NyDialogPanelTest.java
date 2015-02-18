package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing.VelgSakPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TELEFON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SAMTALEREFERAT_TELEFON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe.OVRG;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.TestUtils.createMockSaker;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
public class NyDialogPanelTest extends WicketPageTest {

    private static final String VALGT_ENHET = "valgtEnhet";
    private static final String FNR = "fnr";
    private static final String FORNAVN = "Fornavn";
    private static final String FRITEKST = "fritekst";
    private static final String TRAAD_ID = "traadId";
    private static final Answer<Melding> RETURNER_SAMME_MELDING = new Answer<Melding>() {
        @Override
        public Melding answer(InvocationOnMock invocation) throws Throwable {
            Melding melding = ((Melding) invocation.getArguments()[0]);
            melding.traadId = TRAAD_ID;
            return melding;
        }
    };

    @Captor
    private ArgumentCaptor<Melding> meldingArgumentCaptor;

    @Inject
    protected HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private SakerService sakerService;
    @Inject
    private CmsContentRetriever cmsContentRetriever;

    @InjectMocks
    private NyDialogPanel testNyDialogPanel;

    private Saker saker;
    private GrunnInfo grunnInfo;

    @Before
    public void setUp() {
        grunnInfo = new GrunnInfo(new GrunnInfo.Bruker(FNR, FORNAVN, ""), new GrunnInfo.Saksbehandler("", "", "", ""));
        saker = createMockSaker();
        when(sakerService.hentSaker(anyString())).thenReturn(saker);
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);

        testNyDialogPanel = new NyDialogPanel("id", grunnInfo);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void inneholderReferatspesifikkeKomponenter() {
        wicket.goToPageWith(testNyDialogPanel)
                .should().containComponent(withId("temagruppe").and(ofType(DropDownChoice.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("nydialogform").and(ofType(Form.class)))
                .should().containComponent(withId("tekstfelt").and(ofType(EnhancedTextArea.class)))
                .should().containComponent(withId("send").and(ofType(AjaxButton.class)))
                .should().containComponent(withId("feedback").and(ofType(FeedbackPanel.class)))
                .should().containComponent(withId("kvittering").and(ofType(KvitteringsPanel.class).thatIsInvisible()));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(testNyDialogPanel.getString("nydialogform.temagruppe.Required")));
        assertThat(errorMessages, hasItem(testNyDialogPanel.getString("nydialogform.kanal.Required")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferattypeMedRiktigeVerdierTilHenvendelse() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture());

        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.kanal, is(TELEFON.name()));
        assertThat(melding.meldingstype, is(SAMTALEREFERAT_TELEFON));
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.temagruppe, is(ARBD.name()));
        assertThat(melding.fritekst, is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderOgJournalforerSporsmaltypeMedRiktigeVerdierTilHenvendelse() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        when(henvendelseUtsendingService.sendHenvendelse(any(Melding.class))).then(RETURNER_SAMME_MELDING);

        settISporsmalsModus();

        testNyDialogPanel.getModelObject().valgtSak = saker.getSakerListeFagsak().get(0).saksliste.get(0);

        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .select("velgModus", 1)
                .write("tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture());

        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.kanal, is(TEKST.name()));
        assertThat(melding.meldingstype, is(SPORSMAL_MODIA_UTGAAENDE));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.temagruppe, is(OVRG.name()));
        assertThat(melding.fritekst, is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));
    }

    @Test
    public void girFeilmeldingDersomManSenderSporsmalUtenValgtJournalforingssak() {
        settISporsmalsModus();

        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .select("velgModus", 1)
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().withId("nydialogform"))
                .should().containComponent(thatIsInvisible().ofType(KvitteringsPanel.class));

        verify(henvendelseUtsendingService, never()).sendHenvendelse(any(Melding.class));
    }

    @Test
    public void viserVelgSakPanelForSporsmalDersomManKlikkerValgtSakLenke() {
        settISporsmalsModus();

        wicket.goToPageWith(testNyDialogPanel)
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)))
                .click().link(withId("valgtSakLenke"))
                .should().containComponent(thatIsVisible().and(withId("valgtSakLenke")))
                .should().containComponent(thatIsVisible().and(ofType(VelgSakPanel.class)));
    }

    @Test
    public void skjulerVelgSakPanelForSporsmalDersomManKlikkerAvbryt() {
        settISporsmalsModus();

        wicket.goToPageWith(testNyDialogPanel)
                .click().link(withId("valgtSakLenke"))
                .click().link(withId("avbrytJournalforing"))
                .should().containComponent(thatIsVisible().and(withId("valgtSakLenke")))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

    @Test
    public void skjulerVelgSakPanelForSporsmalDersomManKlikkerVelgerSak() {
        settISporsmalsModus();

        wicket.goToPageWith(testNyDialogPanel)
                .click().link(withId("valgtSakLenke"))
                .inForm(withId("plukkSakForm"))
                .select("valgtSak", 0)
                .submitWithAjaxButton(withId("velgSak"))
                .should().containComponent(thatIsVisible().and(withId("valgtSakLenke")))
                .should().containComponent(thatIsInvisible().and(ofType(VelgSakPanel.class)));
    }

    private void settISporsmalsModus() {
        testNyDialogPanel.getModelObject().modus = HenvendelseVM.Modus.SPORSMAL;
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(lagNyDialogPanelMedKanalSatt())
                .inForm(withId("nydialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 0)
                .select("temagruppe", 1)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("nydialogform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    @Test
    public void telefonSomDefaultKanalHvisSaksbehandlerErTilknyttetKontaktsenter() {
        when(saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()).thenReturn(true);

        wicket.goToPageWith(new NyDialogPanel("id", grunnInfo));

        HenvendelseVM modelObject = (HenvendelseVM) wicket.get().component(withId("nydialogform").and(ofType(Form.class))).getDefaultModelObject();
        assertThat(modelObject.kanal, is((equalTo(TELEFON))));
    }

    @Test
    public void ingenDefaultKanalHvisSaksbehandlerIkkeErTilknyttetKontaktsenter() {
        when(saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()).thenReturn(false);

        wicket.goToPageWith(testNyDialogPanel);

        HenvendelseVM modelObject = (HenvendelseVM) wicket.get().component(withId("nydialogform").and(ofType(Form.class))).getDefaultModelObject();
        assertThat(modelObject.kanal, is(equalTo(null)));
    }

    @Test
    public void skalViseFornavnISubmitKnapp() {
        when(cmsContentRetriever.hentTekst(anyString())).thenReturn("Tekst fra mock-cms %s");

        wicket.goToPageWith(testNyDialogPanel)
                .should().containPatterns(FORNAVN);
    }

    protected NyDialogPanel lagNyDialogPanelMedKanalSatt() {
        testNyDialogPanel = new NyDialogPanel("id", grunnInfo);
        Object nydialogformModel = testNyDialogPanel.get("nydialogform").getDefaultModelObject();
        HenvendelseVM henvendelseVM = (HenvendelseVM) nydialogformModel;
        henvendelseVM.kanal = TELEFON;
        henvendelseVM.temagruppe = ARBD;
        return testNyDialogPanel;
    }

}