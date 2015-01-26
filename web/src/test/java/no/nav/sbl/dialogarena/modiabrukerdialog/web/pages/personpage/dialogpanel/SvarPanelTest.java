package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;


import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.ConsumerServicesMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.EndpointMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.LeggTilbakePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel.TidligereMeldingPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_TELEFON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        ConsumerServicesMockContext.class,
        EndpointMockContext.class})
public class SvarPanelTest extends WicketPageTest {

    public static final String FNR = "fnr";
    public static final String SPORSMAL_ID = "id";
    public static final String TEMAGRUPPE = Temagruppe.FMLI.name();
    public static final String FRITEKST = "fritekst";
    @Captor
    private ArgumentCaptor<Melding> meldingArgumentCaptor;

    @Inject
    protected HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    protected OppgaveBehandlingService oppgaveBehandlingService;

    @InjectMocks
    private TestSvarPanel testSvarPanel;

    @Before
    public void setUp() {
        testSvarPanel = new TestSvarPanel("id", FNR, asList(lagSporsmal()));
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(testSvarPanel)
                .should().containComponent(withId("temagruppe").and(ofType(Label.class)))
                .should().containComponent(withId("sporsmal").and(ofType(TidligereMeldingPanel.class)))
                .should().containComponent(withId("svarliste").and(ofType(ListView.class)))
                .should().containComponent(withId("dato").and(ofType(Label.class)))
                .should().containComponent(withId("kanal").and(ofType(RadioGroup.class)))
                .should().containComponent(withId("kanalbeskrivelse").and(ofType(Label.class)))
                .should().containComponent(withId("svarform"))
                .should().containComponent(withId("leggtilbakepanel").and(ofType(LeggTilbakePanel.class)))
                .should().containComponent(withId("leggtilbake").and(ofType(AjaxLink.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderHenvendelseMedRiktigeFelterTilHenvendelseUtsendingService() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testSvarPanel)
                .inForm(withId("svarform"))
                .write("tekstfelt:text", FRITEKST)
                .select("kanal", 0)
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
    public void senderSvarTilHenvendelseDersomManVelgerTekstSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testSvarPanel)
                .inForm(withId("svarform"))
                .write("tekstfelt:text", FRITEKST)
                .select("kanal", 0)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_SKRIFTLIG));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferatTilHenvendelseDersomManVelgerTelefonSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testSvarPanel)
                .inForm(withId("svarform"))
                .write("tekstfelt:text", FRITEKST)
                .select("kanal", 1)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_TELEFON));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void senderReferatTilHenvendelseDersomManVelgerOppmoteSomKanal() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        wicket.goToPageWith(testSvarPanel)
                .inForm(withId("svarform"))
                .write("tekstfelt:text", FRITEKST)
                .select("kanal", 2)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).sendHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
    }

    @Test
    public void girFeedbackOmPaakrevdeKomponenter() {
        wicket.goToPageWith(testSvarPanel)
                .inForm(withId("svarform"))
                .submitWithAjaxButton(withId("send"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
    }

    @Test
    public void tekstligSvarErValgtSomDefault() {
        wicket.goToPageWith(testSvarPanel)
                .should().containComponent(withId("kanal").and(withModelObject(is(TEKST))));
    }

    @Test
    public void viserTemagruppenFraSporsmalet() {
        wicket.goToPageWith(testSvarPanel)
                .should().containComponent(withId("temagruppe").and(withTextSaying(testSvarPanel.getString(TEMAGRUPPE))));
    }

    @Test
    public void viserKvitteringNaarManSenderInn() {
        wicket.goToPageWith(testSvarPanel)
                .inForm(withId("svarform"))
                .write("tekstfelt:text", FRITEKST)
                .submitWithAjaxButton(withId("send"))
                .should().containComponent(thatIsInvisible().withId("svarform"))
                .should().containComponent(thatIsVisible().ofType(KvitteringsPanel.class));
    }

    @Test
    public void viserTraadToggleLenkeHvisSvarFinnes() {
        wicket.goToPageWith(new TestSvarPanel(SPORSMAL_ID, FNR, asList(lagSporsmal(), lagSvar())))
                .should().containComponent(thatIsVisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void viserIkkeTraadToggleLenkeHvisIngenSvarFinnes() {
        wicket.goToPageWith(testSvarPanel)
                .should().containComponent(thatIsInvisible().and(withId("vistraadcontainer")));
    }

    @Test
    public void togglerVisningAvTraad() {
        wicket.goToPageWith(new TestSvarPanel(SPORSMAL_ID, FNR, asList(lagSporsmal(), lagSvar())))
                .should().containComponent(thatIsInvisible().and(withId("traadcontainer")))
                .onComponent(withId("vistraadcontainer")).executeAjaxBehaviors(BehaviorMatchers.ofType(AjaxEventBehavior.class))
                .should().containComponent(thatIsVisible().and(withId("traadcontainer")));
    }

    @Test
    public void viserLeggTilbakePanel() {
        wicket.goToPageWith(testSvarPanel)
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsVisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void leggTilbakeLenkeHarTekstenLeggTilbake() {
        wicket.goToPageWith(testSvarPanel);

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("svarpanel.avbryt.leggtilbake");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
    }

    @Test
    public void leggTilbakeLenkeHarTekstenAvbryt() {
        wicket.goToPageWith(testSvarPanel);

        Label leggtilbaketekst = wicket.get().component(withId("leggtilbaketekst").and(ofType(Label.class)));
        String labeltekst = (String) leggtilbaketekst.getDefaultModelObject();
        String leggTilbakePropertyTekst = leggtilbaketekst.getString("svarpanel.avbryt.avbryt");

        assertThat(labeltekst, is(equalTo(leggTilbakePropertyTekst)));
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