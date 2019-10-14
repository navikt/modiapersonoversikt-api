package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;


import no.nav.modig.content.ContentRetriever;
import no.nav.modig.wicket.test.matcher.BehaviorMatchers;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.DialogPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning.ENHET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning.SAKSBEHANDLER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel.erTilknyttetAnsatt;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DialogPanelMockContext.class})
public class FortsettDialogPanelTest extends WicketPageTest {

    private static final String FNR = "fnr";
    private static final String FORNAVN = "Fornavn";
    private static final String SPORSMAL_ID = "id";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = Temagruppe.FMLI.name();
    private static final String VALGT_ENHET = "1307";
    private static final String BRUKERS_ENHET = "1234";
    private static final String BEHANDLINGS_ID = "behandlingsId";
    private static final String OPPGAVEID = "oppgaveid";
    private static final String SVARHENVENDELSEID = "svarhenvendelseid";

    @Captor
    private ArgumentCaptor<Melding> meldingArgumentCaptor;
    @Captor
    private ArgumentCaptor<Optional<Sak>> sakArgumentCaptor;
    @Captor
    private ArgumentCaptor<Optional<String>> optionalStringArgumentCaptor;

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private ContentRetriever contentRetriever;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private FortsettDialogPanel testFortsettDialogPanel;

    private GrunnInfo grunnInfo;

    @BeforeEach
    public void setUp() {
        grunnInfo = new GrunnInfo(new Bruker(FNR, FORNAVN, "", "", "", "", ""), new Saksbehandler("", "", ""));
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(VALGT_ENHET);

        testFortsettDialogPanel = new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraBruker()), new Oppgave(null, FNR, SPORSMAL_ID).withSvarHenvendelseId(SVARHENVENDELSEID));
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void inneholderSporsmaalsspefikkeKomponenter() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(withId("temagruppe").and(ofType(Label.class)))
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

        verify(henvendelseUtsendingService).ferdigstillHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.fnrBruker, is(FNR));
        assertThat(melding.navIdent, is(getSubjectHandler().getUid()));
        assertThat(melding.traadId, is(SPORSMAL_ID));
        assertThat(melding.temagruppe, is(TEMAGRUPPE));
        assertThat(melding.kanal, is(Kanal.TEKST.name()));
        assertThat(melding.getFritekst(), is(FRITEKST));
        assertThat(melding.eksternAktor, is(getSubjectHandler().getUid()));
        assertThat(melding.brukersEnhet, is(BRUKERS_ENHET));
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

        verify(henvendelseUtsendingService).ferdigstillHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
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

        verify(henvendelseUtsendingService).ferdigstillHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
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

        verify(henvendelseUtsendingService).ferdigstillHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
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

        verify(henvendelseUtsendingService).ferdigstillHenvendelse(meldingArgumentCaptor.capture(), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
        Melding melding = meldingArgumentCaptor.getValue();
        assertThat(melding.meldingstype, is(SVAR_OPPMOTE));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void senderOppgaveIdTilFerdigstillelseDersomDenneErSatt() throws Exception {
        reset(henvendelseUtsendingService);

        Oppgave oppgave = new Oppgave("oppgaveid", "fnr", "henvendelseid");
        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraBruker()), oppgave.withSvarHenvendelseId(SVARHENVENDELSEID)))
                .inForm(withId("fortsettdialogform"))
                .write("fortsettdialogformelementer:tekstfelt:text", FRITEKST)
                .select("fortsettdialogformelementer:kanal", 1)
                .submitWithAjaxButton(withId("send"));

        verify(henvendelseUtsendingService).ferdigstillHenvendelse(any(Melding.class), optionalStringArgumentCaptor.capture(), any(Optional.class), anyString(), eq(VALGT_ENHET));

        String sendtOppgaveId = optionalStringArgumentCaptor.getValue().get();
        assertThat(sendtOppgaveId, is(oppgave.oppgaveId));

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
                .when(henvendelseUtsendingService).ferdigstillHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
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
    public void viserFeilmeldingDersomSendHenvendelseKasterException() throws Exception {
        doThrow(new Exception()).when(henvendelseUtsendingService).ferdigstillHenvendelse(any(Melding.class), any(Optional.class), any(Optional.class), anyString(), eq(VALGT_ENHET));
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
    public void viserLeggTilbakePanelDersomSporsmalErFraBruker() {
        wicket.goToPageWith(testFortsettDialogPanel)
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsVisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void viserIkkeLeggTilbakeDersomSporsmalIkkeErEtSporsmalFraBruker() {
        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraNAV().withErTilknyttetAnsatt(true)),
                new Oppgave(null, FNR, SPORSMAL_ID)))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")))
                .click().link(withId("leggtilbake"))
                .should().containComponent(thatIsInvisible().and(withId("leggtilbakepanel")));
    }

    @Test
    public void senderAvbrytTilHenvendelseDersomBrukerAvbryterMelding() {
        when(henvendelseUtsendingService.opprettHenvendelse(anyString(), anyString(), anyString())).thenReturn(BEHANDLINGS_ID);
        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraNAV().withErTilknyttetAnsatt(true)),
                new Oppgave(null, FNR, SPORSMAL_ID).withSvarHenvendelseId(BEHANDLINGS_ID)))
                .click().link(withId("leggtilbake"));

        verify(henvendelseUtsendingService, times(1)).avbrytHenvendelse(BEHANDLINGS_ID);
    }

    @Test
    public void skalViseFornavnISubmitKnapp() {
        when(contentRetriever.hentTekst(anyString())).thenReturn("Tekst fra mock-cms %s");

        wicket.goToPageWith(new FortsettDialogPanel("id", grunnInfo, asList(lagSporsmalFraBruker()),
                new Oppgave(null, FNR, SPORSMAL_ID)))
                .should().containPatterns(FORNAVN);
    }

    @Test
    public void tilknyttetAnsattBlirSattTilSaksbehandlerForEnkeltstaaendeSporsmalFraBruker() {
        OppgaveTilknytning oppgaveTilknytning = erTilknyttetAnsatt(singletonList(lagSporsmalFraBruker()));

        assertThat(oppgaveTilknytning, is(SAKSBEHANDLER));
    }

    @Test
    public void tilknyttetAnsattHvisIngenUtgaaendeSporsmal() {
        assertThat(erTilknyttetAnsatt(asList(lagSporsmalFraBruker(), lagSvar(), lagSvar())), is(SAKSBEHANDLER));
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
        sporsmalFraBruker.ferdigstiltDato = DateTime.now().minusDays(2);

        Melding sporsmalFraNAV = lagSporsmalFraNAV();
        sporsmalFraNAV.ferdigstiltDato = DateTime.now();
        sporsmalFraNAV.erTilknyttetAnsatt = true;
        OppgaveTilknytning oppgaveTilknytning = erTilknyttetAnsatt(asList(sporsmalFraBruker, sporsmalFraNAV));

        assertThat(oppgaveTilknytning, is(SAKSBEHANDLER));
    }

    private Melding lagSporsmalFraBruker() {
        Melding sporsmal = new Melding()
                .withType(SPORSMAL_SKRIFTLIG)
                .withId(SPORSMAL_ID)
                .withFerdigstiltDato(now())
                .withBrukersEnhet(BRUKERS_ENHET)
                .withErTilknyttetAnsatt(false);
        sporsmal.temagruppe = TEMAGRUPPE;
        return sporsmal;
    }

    private Melding lagSporsmalFraNAV() {
        Melding sporsmal = new Melding()
                .withType(Meldingstype.SPORSMAL_MODIA_UTGAAENDE)
                .withId(SPORSMAL_ID)
                .withFerdigstiltDato(now());
        sporsmal.temagruppe = TEMAGRUPPE;
        return sporsmal;
    }

    private Melding lagSvar() {
        return new Melding()
                .withFerdigstiltDato(now())
                .withType(SVAR_SKRIFTLIG)
                .withFritekst(new Fritekst("fritekst", new no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler("", "", ""), now()))
                .withTemagruppe(TEMAGRUPPE);
    }

}