package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.test.EventGenerator;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.DialogPanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.velgdialogpanel.VelgDialogPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ARBD;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_AVBRUTT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_LENKE_VALGT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel.LEGG_TILBAKE_FERDIG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DialogPanelMockContext.class})
public class DialogPanelTest extends WicketPageTest {

    private static final String ID = "id";
    private static final String OPPGAVEID_VERDI = "123";
    private static final String HENVENDELSEID_VERDI = "321";
    private static final String OPPGAVEID_FOR_SPORSMAL = "oppgaveid";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    @Before
    public void setUp() {
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(lagMelding()));
    }

    private GrunnInfo getMockGrunnInfo() {
        GrunnInfo.Bruker bruker = new GrunnInfo.Bruker("10108000398", "test", "testesen", "navKontorX");
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
        settSessionVerdier(OPPGAVEID_VERDI, HENVENDELSEID_VERDI, false);

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .should().containComponent(ofType(VelgDialogPanel.class));
    }

    @Test
    public void initialisererMedFortsettDialogPanelDersomOppgaveIdOgHenvendelseIdOgBesvaresParametereErSatt() {
        settSessionVerdier(OPPGAVEID_VERDI, HENVENDELSEID_VERDI, true);

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .should().containComponent(ofType(FortsettDialogPanel.class));
    }

    @Test
    public void tilordnerOppgaveHvisOppgaveIdOgHenvendelseIdOgBesvaresParametereErSatt() throws OppgaveBehandlingService.FikkIkkeTilordnet {
        settSessionVerdier(OPPGAVEID_VERDI, HENVENDELSEID_VERDI, true);
        reset(oppgaveBehandlingService);

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .should().containComponent(ofType(FortsettDialogPanel.class));

        verify(oppgaveBehandlingService, times(1)).tilordneOppgaveIGsak(eq(OPPGAVEID_VERDI), any(Temagruppe.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventUtenParametereSatt() {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        Optional<String> oppgaveId = (Optional<String>) Whitebox.getInternalState(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId.isPresent(), is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventForEnkeltstaaendeSpormalFraBrukerUtenParametereSatt() {
        Melding spsm = lagBrukerSporsmalMedOppgaveId();
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(spsm));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        Optional<String> oppgaveId = (Optional<String>) Whitebox.getInternalState(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId.get(), is(spsm.oppgaveId));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventDersomOppgaveIdOgHenvendelseIdParametereErSatt() {
        settSessionVerdier(OPPGAVEID_VERDI, HENVENDELSEID_VERDI, false);
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING, HENVENDELSEID_VERDI))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        Optional<String> oppgaveId = (Optional<String>) Whitebox.getInternalState(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId.get(), is(OPPGAVEID_VERDI));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fortsettDialogPanelHarRiktigOppgaveIdVedSVAR_PAA_MELDINGEventDersomOppgaveIdOgHenvendelseIdParametereErSattMenTraadIdIkkeErLik() {
        settSessionVerdier(OPPGAVEID_VERDI, HENVENDELSEID_VERDI, false);
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING, "ikkeSammeTraadIdSomMeldingen"))
                .should().inAjaxResponse().haveComponents(ofType(FortsettDialogPanel.class));

        FortsettDialogPanel fortsettDialogPanel = wicket.get().component(ofType(FortsettDialogPanel.class));
        Optional<String> oppgaveId = (Optional<String>) Whitebox.getInternalState(fortsettDialogPanel, "oppgaveId");
        assertThat(oppgaveId.isPresent(), is(false));
    }

    @Test
    public void tilordnerIkkeOppgaveIGsakDersomerTraadenIkkeErEtEnkeltstaaendeSporsmalFraBrukerVedEventetSVAR_PAA_MELDING() throws OppgaveBehandlingService.FikkIkkeTilordnet {
        reset(oppgaveBehandlingService);

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService, never()).tilordneOppgaveIGsak(anyString(), any(Temagruppe.class));
    }

    @Test
    public void tilordnerOppgaveIGsakDersomerTraadenErEtEnkeltstaaendeSporsmalFraBrukerVedEventetSVAR_PAA_MELDING() throws OppgaveBehandlingService.FikkIkkeTilordnet {
        reset(oppgaveBehandlingService);

        Melding spsm = lagBrukerSporsmalMedOppgaveId();
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(spsm));

        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(Events.SporsmalOgSvar.SVAR_PAA_MELDING));

        verify(oppgaveBehandlingService).tilordneOppgaveIGsak(spsm.oppgaveId, ARBD);
    }

    @Test
    public void erstatterDialogPanelMedRiktigPanelVedGitteEvents() {
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(NY_DIALOG_LENKE_VALGT, NyDialogPanel.class);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(Events.SporsmalOgSvar.SVAR_AVBRUTT, VelgDialogPanel.class);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(LEGG_TILBAKE_FERDIG, VelgDialogPanel.class);
        assertErstatterDialogPanelMedNyDialogPanelVedEvent(NY_DIALOG_AVBRUTT, VelgDialogPanel.class);
    }

    private void assertErstatterDialogPanelMedNyDialogPanelVedEvent(String event, Class panelSomSKalVises) {
        wicket.goToPageWith(new DialogPanel(ID, getMockGrunnInfo()))
                .sendEvent(createEvent(event))
                .should().inAjaxResponse().haveComponents(ofType(panelSomSKalVises));
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