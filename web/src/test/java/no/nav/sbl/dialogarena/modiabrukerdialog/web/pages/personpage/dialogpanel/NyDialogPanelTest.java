package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing.VelgSakPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Captor
    private ArgumentCaptor<Melding> meldingArgumentCaptor;
    @Captor
    private ArgumentCaptor<Optional<Sak>> sakArgumentCaptor;

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
        grunnInfo = new GrunnInfo(new GrunnInfo.Bruker(FNR, FORNAVN, "", ""), new GrunnInfo.Saksbehandler("", "", ""));
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
                .should().containComponent(withId("kvittering").and(ofType(KvitteringsPanel.class).thatIsInvisible()))
                .should().containComponent(withId("brukerKanSvareContainer").and(ofType(WebMarkupContainer.class).thatIsInvisible()));
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
    public void senderReferattypeMedRiktigeVerdierTilHenvendelse() throws Exception {
        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class));

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
    public void senderOgJournalforerSporsmaltypeMedRiktigeVerdierTilHenvendelse() throws Exception {
        settISporsmalsModus();

        Sak sak = saker.getSakerListeFagsak().get(0).saksliste.get(0);
        testNyDialogPanel.getModelObject().valgtSak = sak;

        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .select("velgModus", 1)
                .write("tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), sakArgumentCaptor.capture());

        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.kanal, is(TEKST.name()));
        assertThat(melding.meldingstype, is(SPORSMAL_MODIA_UTGAAENDE));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.temagruppe, is(OVRG.name()));
        assertThat(melding.fritekst, is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));

        Sak sendtSak = sakArgumentCaptor.getValue().get();
        assertThat(sendtSak, is(sak));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void girFeilmeldingDersomManSenderSporsmalUtenValgtJournalforingssak() throws Exception {
        settISporsmalsModus();

        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .select("velgModus", 1)
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().withId("nydialogform"))
                .should().containComponent(thatIsInvisible().ofType(KvitteringsPanel.class));

        verify(henvendelseUtsendingService, never()).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
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

    @Test
    public void viserDisabledCheckedBrukerKanSvareDersomISporsmalsmodus() {
        settISporsmalsModus();

        wicket.goToPageWith(testNyDialogPanel)
                .should().containComponent(withId("brukerKanSvareContainer").thatIsVisible());
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
    @SuppressWarnings("unchecked")
    public void garTilKvitteringssideOgsaaDersomJournalforingKasterException() throws Exception {
        doThrow(new JournalforingFeilet()).when(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 0)
                .select("temagruppe", 1)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().and(ofType(KvitteringsPanel.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void viserFeilmeldingDersomSendHenvendelseKasterException() throws Exception {
        doThrow(new Exception()).when(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class));
        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 0)
                .select("temagruppe", 1)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().and(ofType(KvitteringsPanel.class)))
                .should().containComponent(thatIsVisible().and(ofType(FeedbackPanel.class)));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(testNyDialogPanel.getString("dialogpanel.feilmelding.journalforing")));
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