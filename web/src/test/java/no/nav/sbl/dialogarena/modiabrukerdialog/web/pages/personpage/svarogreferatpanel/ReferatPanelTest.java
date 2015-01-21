package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.journalforing.JournalforingsPanelVelgSak;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TELEFON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SAMTALEREFERAT_TELEFON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.TestUtils.createMockSaker;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
public class ReferatPanelTest extends WicketPageTest {

    public static final String VALGT_ENHET = "valgtEnhet";
    public static final String FNR = "fnr";
    public static final String FRITEKST = "fritekst";
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
    protected HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private SakerService sakerService;

    @InjectMocks
    private TestReferatPanel testReferatPanel;

    private Saker saker;

    @Before
    public void setUp() {
        saker = createMockSaker();
        when(sakerService.hentSaker(anyString())).thenReturn(saker);
    }

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
    public void senderReferattypeMedRiktigeVerdierTilHenvendelse() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        wicket.goToPageWith(testReferatPanel)
                .inForm(withId("referatform"))
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
        assertThat(melding.tilknyttetEnhet, is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderSporsmaltypeMedRiktigeVerdierTilHenvendelse() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);
        when(henvendelseUtsendingService.sendHenvendelse(any(Melding.class))).then(RETURNER_SAMME_MELDING);
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        testReferatPanel.getModelObject().valgtSak = saker.getSakerListeFagsak().get(0).saksliste.get(0);

        wicket.goToPageWith(testReferatPanel)
                .inForm(withId("referatform"))
                .select("velgModus", 1)
                .write("tekstfelt:text", FRITEKST)
                .select("temagruppe", 0)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture());

        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.kanal, is(TEKST.name()));
        assertThat(melding.meldingstype, is(SPORSMAL_MODIA_UTGAAENDE));
        assertThat(melding.tilknyttetEnhet, is(VALGT_ENHET));
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.temagruppe, is(ARBD.name()));
        assertThat(melding.fritekst, is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));
    }

    @Test
    public void girFeilmeldingDersomManSenderSporsmalUtenValgtJournalforingssak() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        settISporsmalsModus();

        wicket.goToPageWith(testReferatPanel)
                .inForm(withId("referatform"))
                    .select("velgModus", 1)
                    .write("tekstfelt:text", FRITEKST)
                    .select("temagruppe", 0)
                    .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsVisible().withId("referatform"))
                .should().containComponent(thatIsInvisible().ofType(KvitteringsPanel.class));

        verify(henvendelseUtsendingService, never()).sendHenvendelse(any(Melding.class));
    }

    @Test
    public void viserJournalforingPanelForSporsmalVelgSakDersomManKlikkerValgtSakLenke() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        settISporsmalsModus();

        wicket.goToPageWith(testReferatPanel)
                .should().containComponent(thatIsInvisible().and(ofType(JournalforingsPanelVelgSak.class)))
                .click().link(withId("valgtSakLenke"))
                .should().containComponent(thatIsInvisible().and(withId("valgtSakLenke")))
                .should().containComponent(thatIsVisible().and(ofType(JournalforingsPanelVelgSak.class)));
    }

    @Test
    public void skjulerJournalforingPanelVelgSakForSporsmalDersomManKlikkerAvbryt() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        settISporsmalsModus();

        wicket.goToPageWith(testReferatPanel)
                .click().link(withId("valgtSakLenke"))
                .click().link(withId("avbrytJournalforing"))
                .should().containComponent(thatIsVisible().and(withId("valgtSakLenke")))
                .should().containComponent(thatIsInvisible().and(ofType(JournalforingsPanelVelgSak.class)));
    }

    @Test
    public void skjulerJournalforingPanelVelgSakForSporsmalDersomManKlikkerVelgerSak() {
        testReferatPanel = new TestReferatPanel("id", FNR);
        MockitoAnnotations.initMocks(this);

        settISporsmalsModus();

        wicket.goToPageWith(testReferatPanel)
                .click().link(withId("valgtSakLenke"))
                .inForm(withId("plukkSakForm"))
                    .select("valgtSak", 0)
                    .submitWithAjaxButton(withId("velgSak"))
                .should().containComponent(thatIsVisible().and(withId("valgtSakLenke")))
                .should().containComponent(thatIsInvisible().and(ofType(JournalforingsPanelVelgSak.class)));
    }

    private void settISporsmalsModus() {
        testReferatPanel.getModelObject().modus = HenvendelseVM.Modus.SPORSMAL;
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