package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService.FikkIkkeTilordnet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.DialogPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.velgdialogpanel.VelgDialogPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_AVBRUTT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_LENKE_VALGT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DialogPanelMockContext.class})
public class DialogPanelTest extends WicketPageTest {

    private static final String ID = "id";
    private static final String OPPGAVEID_VERDI = "123";
    private static final String HENVENDELSEID_VERDI = "321";
    private static final String OPPGAVEID_FOR_SPORSMAL = "oppgaveid";
    public static final String SAKSBEHANDLERS_VALGTE_ENHET = "4300";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingServiceMock;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingServiceMock;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @BeforeEach
    public void setUp() {
        reset(oppgaveBehandlingServiceMock);
        when(henvendelseUtsendingServiceMock.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(lagMelding()));
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(SAKSBEHANDLERS_VALGTE_ENHET);
    }

    private GrunnInfo getMockGrunnInfo() {
        GrunnInfo.Bruker bruker = new GrunnInfo.Bruker("10108000398", "test", "testesen", "navKontorX", "1234", "", "M");
        GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler("enhetX", "fornavn", "etternavn");
        return new GrunnInfo(bruker, saksbehandler);
    }

    @Test
    public void starterPanelUtenFeil() {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()));
    }

    @Test
    public void initialisererMedNyDialogPanelDersomIngenParametereErSatt() {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .should().containComponent(ofType(NyDialogPanel.class));
    }

    @Test
    public void initialisererMedVelgDialogPanelDersomOppgaveIdOgHenvendelseIdParametereErSatt() {
        DialogSession.read(wicket.tester.getSession())
                .withURLParametre(
                        new PageParameters()
                                .set(OPPGAVEID, OPPGAVEID_VERDI)
                                .set(HENVENDELSEID, HENVENDELSEID_VERDI)
                );

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .should().containComponent(ofType(VelgDialogPanel.class));
    }

    @Test
    public void initialisererMedFortsettDialogPanelDersomOppgaveIdOgHenvendelseIdOgBesvaresParametereErSatt() {
        DialogSession.read(wicket.tester.getSession())
                .withPlukkedeOppgaver(singletonList(new Oppgave(OPPGAVEID_VERDI, FNR, HENVENDELSEID_VERDI)));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .should().containComponent(ofType(FortsettDialogPanel.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventUtenParametereSatt() {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        String oppgaveId = (String) getField(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId, is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventForEnkeltstaaendeSpormalFraBrukerUtenParametereSatt() {
        Melding spsm = lagBrukerSporsmalMedOppgaveId();
        when(henvendelseUtsendingServiceMock.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(spsm));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        String oppgaveId = (String) getField(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId, is(spsm.oppgaveId));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventDersomOppgaveIdOgHenvendelseIdParametereErSatt() {
        when(henvendelseUtsendingServiceMock.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(lagBrukerSporsmalMedOppgaveId()));

        DialogSession.read(wicket.tester.getSession())
                .withURLParametre(
                        new PageParameters()
                                .set(OPPGAVEID, OPPGAVEID_FOR_SPORSMAL)
                                .set(HENVENDELSEID, HENVENDELSEID_VERDI)
                );
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING, HENVENDELSEID_VERDI))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        String oppgaveId = (String) getField(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId, is(OPPGAVEID_FOR_SPORSMAL));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventDersomOppgaveIdOgHenvendelseIdParametereErSattMenTraadIdIkkeErLik() {
        DialogSession.read(wicket.tester.getSession())
                .withURLParametre(
                        new PageParameters()
                                .set(OPPGAVEID, OPPGAVEID_VERDI)
                                .set(HENVENDELSEID, HENVENDELSEID_VERDI)
                );
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING, "ikkeSammeTraadIdSomMeldingen"))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        String oppgaveId = (String) getField(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId, is(nullValue()));
    }

    @Test
    public void tilordnerIkkeOppgaveIGsakDersomerTraadenIkkeErEtEnkeltstaaendeSporsmalFraBrukerVedEventetSVAR_PAA_MELDING() throws FikkIkkeTilordnet {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING));

        verify(oppgaveBehandlingServiceMock, never()).tilordneOppgaveIGsak(anyString(), any(Temagruppe.class), anyString());
    }

    @Test
    public void tilordnerOppgaveIGsakDersomerTraadenErEtEnkeltstaaendeSporsmalFraBrukerVedEventetSVAR_PAA_MELDING() throws FikkIkkeTilordnet {
        Melding spsm = lagBrukerSporsmalMedOppgaveId();
        when(henvendelseUtsendingServiceMock.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(spsm));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING));

        verify(oppgaveBehandlingServiceMock).tilordneOppgaveIGsak(spsm.oppgaveId, ARBD, SAKSBEHANDLERS_VALGTE_ENHET);
    }

    @Test
    public void tilordnerOppgaveIGsakDersomSammenslattTraad() throws FikkIkkeTilordnet {
        Melding melding1 = lagBrukerSporsmalMedOppgaveId();
        Melding melding2 = lagBrukerSporsmalMedOppgaveId();
        when(henvendelseUtsendingServiceMock.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(melding1, melding2));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING));

        verify(oppgaveBehandlingServiceMock).tilordneOppgaveIGsak(melding1.oppgaveId, ARBD, SAKSBEHANDLERS_VALGTE_ENHET);
    }

    @Test
    public void tilordnerOppgaveIGsakDersomEnkeltstaaendeSporsmalMedDelvisSvar() throws FikkIkkeTilordnet {
        when(henvendelseUtsendingServiceMock.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(lagBrukerSporsmalMedOppgaveId(), lagDelvisSvar()));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING));

        verify(oppgaveBehandlingServiceMock).tilordneOppgaveIGsak(OPPGAVEID_FOR_SPORSMAL, ARBD, SAKSBEHANDLERS_VALGTE_ENHET);
    }

    @Test
    public void erstatterDialogPanelMedRiktigPanelVedGitteEvents() {
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(NY_DIALOG_LENKE_VALGT, NyDialogPanel.class);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(Events.SporsmalOgSvar.SVAR_AVBRUTT, VelgDialogPanel.class);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(LEGG_TILBAKE_FERDIG, VelgDialogPanel.class);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(NY_DIALOG_AVBRUTT, VelgDialogPanel.class);
    }

    private void assertErstatterDialogPanelMedNyDialogPanelVedEvent(String event, Class panelSomSkalVises) {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(event))
                .should().inAjaxResponse().haveComponents(ofType(panelSomSkalVises));
    }

    private void settSessionVerdier(String oppgaveIdVerdi, String henvendelseIdVerdi, Boolean fortsettDialogModusVerdi) {
        wicket.tester.getSession().setAttribute(OPPGAVEID, oppgaveIdVerdi);
        wicket.tester.getSession().setAttribute(HENVENDELSEID, henvendelseIdVerdi);
        wicket.tester.getSession().setAttribute(BESVARES, fortsettDialogModusVerdi.toString());
    }

    private Melding lagMelding() {
        return new Melding(HENVENDELSEID_VERDI, SPORSMAL_MODIA_UTGAAENDE, DateTime.now())
                .withTraadId(HENVENDELSEID_VERDI)
                .withTemagruppe(ARBD.name())
                .withErTilknyttetAnsatt(false);
    }

    private Melding lagDelvisSvar() {
        return new Melding()
                .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                .withFerdigstiltDato(DateTime.now());
    }

    private Melding lagBrukerSporsmalMedOppgaveId() {
        return new Melding(HENVENDELSEID_VERDI, Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now())
                .withTraadId(HENVENDELSEID_VERDI)
                .withTemagruppe("ARBD")
                .withOppgaveId(OPPGAVEID_FOR_SPORSMAL);
    }

    private EventGenerator createEvent(final String eventNavn) {
        return new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(eventNavn, "");
            }
        };
    }

    private EventGenerator createEvent(final String eventNavn, final Object payload) {
        return new EventGenerator() {
            @Override
            public Object createEvent(AjaxRequestTarget target) {
                return new NamedEventPayload(eventNavn, payload);
            }
        };
    }

}