package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.Henvendelsetype.REFERAT_TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe.ARBD;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
public class ReferatPanelTest extends WicketPageTest {

    public static final String VALGT_ENHET = "valgtEnhet";
    public static final String FNR = "fnr";
    public static final String FRITEKST = "fritekst";

    @Captor
    private ArgumentCaptor<Henvendelse> henvendelseArgumentCaptor;

    @Inject
    protected HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private TestReferatPanel testReferatPanel;

    @Test
    public void inneholderReferatspesifikkeKomponenter() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        wicket.goToPageWith(testReferatPanel)
                .should().containComponent(withId("temagruppe").and(ofType(DropDownChoice.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("referatform").and(ofType(Form.class)))
                .should().containComponent(withId("tekstfelt").and(ofType(EnhancedTextArea.class)))
                .should().containComponent(withId("send").and(ofType(AjaxButton.class)))
                .should().containComponent(withId("feedback").and(ofType(FeedbackPanel.class)))
                .should().containComponent(withId("kvittering").and(ofType(KvitteringsPanel.class).thatIsInvisible()));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        wicket.goToPageWith(testReferatPanel)
                .inForm(withId("referatform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(testReferatPanel.getString("referatform.temagruppe.Required")));
        assertThat(errorMessages, hasItem(testReferatPanel.getString("referatform.kanal.Required")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skalSendeReferattypeMedRiktigeVerdierTilHenvendelse() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        wicket.goToPageWith(testReferatPanel)
                .inForm(withId("referatform"))
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(henvendelseArgumentCaptor.capture());

        Henvendelse henvendelse = henvendelseArgumentCaptor.getValue();
        assertThat(henvendelse.kanal, is(TELEFON.name()));
        assertThat(henvendelse.type, is(REFERAT_TELEFON));
        assertThat(henvendelse.fnr, is(FNR));
        assertThat(henvendelse.navIdent, is(getSubjectHandler().getUid()));
        assertThat(henvendelse.temagruppe, is(ARBD.name()));
        assertThat(henvendelse.fritekst, is(FRITEKST));
        assertThat(henvendelse.eksternAktor, is(getSubjectHandler().getUid()));
        assertThat(henvendelse.tilknyttetEnhet, is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void skalSendeSporsmaltypeMedRiktigeVerdierTilHenvendelse() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        wicket.goToPageWith(testReferatPanel)
                .inForm(withId("referatform"))
                .select("velgModus", 1)
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(henvendelseArgumentCaptor.capture());

        Henvendelse henvendelse = henvendelseArgumentCaptor.getValue();
        assertThat(henvendelse.kanal, is(TEKST.name()));
        assertThat(henvendelse.type, is(SPORSMAL_MODIA_UTGAAENDE));
        assertThat(henvendelse.tilknyttetEnhet, is(VALGT_ENHET));
        assertThat(henvendelse.fnr, is(FNR));
        assertThat(henvendelse.navIdent, is(getSubjectHandler().getUid()));
        assertThat(henvendelse.temagruppe, is(ARBD.name()));
        assertThat(henvendelse.fritekst, is(FRITEKST));
        assertThat(henvendelse.eksternAktor, is(getSubjectHandler().getUid()));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(lagReferatPanelMedKanalSatt())
                .inForm(withId("referatform"))
                .write("tekstfelt:text", "dette er en fritekst")
                .select("kanal", 0)
                .select("temagruppe", 1)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("referatform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    @Test
    public void telefonSomDefaultKanalHvisSaksbehandlerErTilknyttetKontaktsenter() {
        when(saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()).thenReturn(true);

        wicket.goToPageWith(new TestReferatPanel("id", FNR));

        HenvendelseVM modelObject = (HenvendelseVM) wicket.get().component(withId("referatform").and(ofType(Form.class))).getDefaultModelObject();
        assertThat(modelObject.kanal, is((equalTo(TELEFON))));
    }

    @Test
    public void ingenDefaultKanalHvisSaksbehandlerIkkeErTilknyttetKontaktsenter() {
        when(saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()).thenReturn(false);

        wicket.goToPageWith(new TestReferatPanel("id", FNR));

        HenvendelseVM modelObject = (HenvendelseVM) wicket.get().component(withId("referatform").and(ofType(Form.class))).getDefaultModelObject();
        assertThat(modelObject.kanal, is(equalTo(null)));
    }

    protected TestReferatPanel lagReferatPanelMedKanalSatt() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        Object referatformModel = testReferatPanel.get("referatform").getDefaultModelObject();
        HenvendelseVM henvendelseVM = (HenvendelseVM) referatformModel;
        henvendelseVM.kanal = TELEFON;
        henvendelseVM.temagruppe = ARBD;
        return testReferatPanel;
    }

}