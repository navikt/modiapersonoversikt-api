package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.DialogPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SAMTALEREFERAT_TELEFON;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DialogPanelMockContext.class})
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
    private ContentRetriever contentRetriever;

    @InjectMocks
    private NyDialogPanel testNyDialogPanel;

    private GrunnInfo grunnInfo;

    @Before
    public void setUp() {
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        grunnInfo = new GrunnInfo(new GrunnInfo.Bruker(FNR, FORNAVN, "", "", "", "", ""), new GrunnInfo.Saksbehandler("", "", ""));
        testNyDialogPanel = new NyDialogPanel("id", grunnInfo);

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);
        when(saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()).thenReturn(false);
        initMocks(this);
    }

    @Test
    public void inneholderReferatspesifikkeKomponenter() {
        wicket.goToPageWith(new NyDialogPanel("id", grunnInfo))
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
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        reset(henvendelseUtsendingService);

        wicket.goToPageWith(testNyDialogPanel)
                .inForm(withId("nydialogform"))
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class), eq(VALGT_ENHET));

        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.kanal, is(TELEFON.name()));
        assertThat(melding.meldingstype, is(SAMTALEREFERAT_TELEFON));
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.temagruppe, is(ARBD.name()));
        assertThat(melding.getFritekst(), is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));
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
        System.setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
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
    public void viserFeilmeldingDersomSendHenvendelseKasterException() throws Exception {
        doThrow(new Exception()).when(henvendelseUtsendingService).sendHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class), eq(VALGT_ENHET));
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
        wicket.goToPageWith(testNyDialogPanel);

        HenvendelseVM modelObject = (HenvendelseVM) wicket.get().component(withId("nydialogform").and(ofType(Form.class))).getDefaultModelObject();
        assertThat(modelObject.kanal, is(equalTo(null)));
    }

    @Test
    public void skalViseFornavnISubmitKnapp() {
        when(contentRetriever.hentTekst(anyString())).thenReturn("Tekst fra mock-cms %s");

        wicket.goToPageWith(testNyDialogPanel)
                .should().containPatterns(FORNAVN);
    }

    @Test
    public void saksbehandlerSomDefaultOppgaveTilknytning() throws Exception {
        NyDialogPanel nyDialogPanel = new NyDialogPanel("id", grunnInfo);
        assertThat(nyDialogPanel.getModelObject().oppgaveTilknytning, is(OppgaveTilknytning.SAKSBEHANDLER));
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