package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.frontend.ConditionalJavascriptResource;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.LamellPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSide;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.SvarOgReferatVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.ReferatPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.timeout.TimeoutBoks;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow.getJavascriptSaveButtonFocus;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel.LEGG_TILBAKE_UTFORT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel.SVAR_AVBRUTT;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import static org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Denne klassen brukes til å vise informasjon om en bruker. Visningen består av lameller, widgets og paneler.
 */
public class PersonPage extends BasePage {

    public static final String VALGT_OPPGAVE_ID_ATTR = "valgt-oppgave-id";
    public static final String VALGT_OPPGAVE_FNR_ATTR = "valgt-oppgave-fnr";
    public static final String SVAR_OG_REFERAT_PANEL_ID = "svarOgReferatPanel";
    public static final String OPPGAVEID = "oppgaveid";
    public static final String HENVENDELSEID = "henvendelseid";
    public static final ConditionalJavascriptResource RESPOND_JS = new ConditionalJavascriptResource(new PackageResourceReference(PersonPage.class, "respond.min.js"), "lt IE 9");
    public static final ConditionalCssResource INTERN_IE = new ConditionalCssResource(new CssResourceReference(PersonPage.class, "personpage_ie.css"), "screen", "lt IE 10");
    public static final PackageResourceReference SVAR_OG_REFERATPANEL_LESS = new PackageResourceReference(SvarOgReferatVM.class, "SvarOgReferatPanel.less");
    public static final JavaScriptResourceReference SELECTMENU_JS = new JavaScriptResourceReference(SvarOgReferatVM.class, "jquery-ui-selectmenu.min.js");
    private static final Logger logger = getLogger(PersonPage.class);
    private final String fnr;
    @Inject
    protected HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    protected OppgaveBehandlingService oppgaveBehandlingService;
    private SjekkForlateSideAnswer answer;
    private RedirectModalWindow redirectPopup;
    private LamellContainer lamellContainer;
    private HentPersonPanel hentPersonPanel;
    private Button searchToggleButton;
    private NullstillLink nullstillLink;
    private Component svarOgReferatPanel;

    public PersonPage(PageParameters pageParameters) {
        fnr = pageParameters.get("fnr").toString(null);
        instansierFelter();
        add(
                hentPersonPanel,
                searchToggleButton,
                nullstillLink,
                lamellContainer,
                redirectPopup,
                new SaksbehandlerInnstillingerPanel("saksbehandlerInnstillingerPanel"),
                new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerInnstillingerToggler"),
                new PlukkOppgavePanel("plukkOppgave"),
                new PersonsokPanel("personsokPanel").setVisible(true),
                new VisittkortPanel("visittkort", fnr).setVisible(true),
                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnr).setVisible(true),
                svarOgReferatPanel,
                new TimeoutBoks("timeoutBoks", fnr)
        );
        erstattReferatPanelMedSvarPanelBasertPaaOppgaveIdParameter(pageParameters);
    }

    private void instansierFelter() {
        answer = new SjekkForlateSideAnswer();
        redirectPopup = createModalWindow("modal");
        lamellContainer = new LamellContainer("lameller", fnr);
        hentPersonPanel = (HentPersonPanel) new HentPersonPanel("searchPanel").setOutputMarkupPlaceholderTag(true);
        searchToggleButton = (Button) new Button("toggle-sok").setOutputMarkupPlaceholderTag(true);
        nullstillLink = (NullstillLink) new NullstillLink("nullstill").setOutputMarkupPlaceholderTag(true);
        svarOgReferatPanel = new ReferatPanel(SVAR_OG_REFERAT_PANEL_ID, fnr);
    }

    private void erstattReferatPanelMedSvarPanelBasertPaaOppgaveIdParameter(PageParameters pageParameters) {
        StringValue oppgaveId = pageParameters.get(OPPGAVEID);
        StringValue henvendelseId = pageParameters.get(HENVENDELSEID);
        if (!oppgaveId.isEmpty()) {
            if (!henvendelseId.isEmpty()) {
                visSvarPanelBasertPaaHenvendelsesId(henvendelseId.toString(), oppgaveId.toString());
            } else {
                visSvarPanelBasertPaaOppgaveIdForSporsmal(oppgaveId.toString());
            }
        }
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String fnr) {
        handleRedirect(target, new PageParameters().set("fnr", fnr), PersonPage.class);
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, String fnr) {
        getSession().setAttribute(HENT_PERSON_BEGRUNNET, true);
        refreshKjerneinfo(target, fnr);
    }

    @RunOnEvents(GOTO_HENT_PERSONPAGE)
    public void gotoHentPersonPage(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(HentPersonPage.class, new PageParameters().set("error", query));
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        try {
            lamellContainer.handleFeedItemEvent(event, feedItemPayload);
        } catch (ApplicationException e) {
            logger.warn("Burde ikke skje, klarte ikke håndtere feeditemevent: {}", e.getMessage(), e);
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

    @RunOnEvents(LAMELL_LINK_CLICKED)
    public void lamellLinkClicked(AjaxRequestTarget target, IEvent<?> event, LamellPayload lamellPayload) {
        try {
            lamellContainer.handleLamellLinkClicked(lamellPayload);
        } catch (ApplicationException e) {
            logger.warn("Burde ikke skje, klarte ikke håndtere lamellLinkClicked: {}", e.getMessage(), e);
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

    @RunOnEvents(WIDGET_HEADER_CLICKED)
    public void widgetHeaderClicked(AjaxRequestTarget target, IEvent<?> event, WidgetHeaderPayload widgetHeaderPayload) {
        try {
            lamellContainer.handleWidgetHeaderEvent(event, widgetHeaderPayload);
        } catch (ApplicationException e) {
            logger.warn("Burde ikke skje, klarte ikke håndtere widgetheaderevent: {}", e.getMessage(), e);
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

    @RunOnEvents(WIDGET_LINK_CLICKED)
    public void widgetLinkClicked(AjaxRequestTarget target, String linkId) {
        try {
            lamellContainer.handleWidgetItemEvent(linkId);
        } catch (ApplicationException e) {
            logger.warn("Burde ikke skje, klarte ikke håndtere widgetLink: {}", e.getMessage(), e);
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

    @RunOnEvents(PERSONSOK_FNR_CLICKED)
    public void personsokresultatClicked(AjaxRequestTarget target, String query) {
        send(getPage(), DEPTH, new NamedEventPayload(FNR_CHANGED, query));
    }

    @RunOnEvents(HENTPERSON_FODSELSNUMMER_IKKE_TILGANG)
    public void personsokIkkeTilgang(AjaxRequestTarget target, String query) {
        send(getPage(), BREADTH, new NamedEventPayload(FODSELSNUMMER_IKKE_TILGANG, query));
    }

    public void visSvarPanelBasertPaaOppgaveIdForSporsmal(String oppgaveId) {
        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmalFromOppgaveId(fnr, oppgaveId);
        erstattReferatPanelMedSvarPanel(sporsmal, henvendelseUtsendingService.getSvarEllerReferatForSporsmal(fnr, sporsmal.id), optional(oppgaveId));
    }

    private void visSvarPanelBasertPaaHenvendelsesId(String henvendelseId, String oppgaveId) {
        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmal(henvendelseId);
        erstattReferatPanelMedSvarPanel(sporsmal, henvendelseUtsendingService.getSvarEllerReferatForSporsmal(fnr, henvendelseId), optional(oppgaveId));
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void visSvarPanelBasertPaaSporsmalId(AjaxRequestTarget target, String sporsmalId) {
        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmal(sporsmalId);
        List<SvarEllerReferat> svar = henvendelseUtsendingService.getSvarEllerReferatForSporsmal(fnr, sporsmalId);
        Optional<String> oppgaveId = none();
        if (sporsmaletIkkeErBesvartTidligere(svar)) {
            oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmal.oppgaveId);
            oppgaveId = optional(sporsmal.oppgaveId);
        }
        erstattReferatPanelMedSvarPanel(sporsmal, svar, oppgaveId);
        target.add(svarOgReferatPanel);
    }

    private void erstattReferatPanelMedSvarPanel(Sporsmal sporsmal, List<SvarEllerReferat> svarTilSporsmal, Optional<String> oppgaveId) {
        svarOgReferatPanel = svarOgReferatPanel.replaceWith(new SvarPanel(SVAR_OG_REFERAT_PANEL_ID, fnr, sporsmal, svarTilSporsmal, oppgaveId));
    }

    private boolean sporsmaletIkkeErBesvartTidligere(List<SvarEllerReferat> svar) {
        return svar.isEmpty();
    }

    @RunOnEvents({MELDING_SENDT_TIL_BRUKER, LEGG_TILBAKE_UTFORT, SVAR_AVBRUTT})
    public void visReferatPanel(AjaxRequestTarget target) {
        svarOgReferatPanel = svarOgReferatPanel.replaceWith(new ReferatPanel(SVAR_OG_REFERAT_PANEL_ID, fnr));
        target.add(svarOgReferatPanel);
    }

    @RunOnEvents({MELDING_SENDT_TIL_BRUKER, LEGG_TILBAKE_UTFORT})
    public void slettPlukketOppgaveFraSession() {
        getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, null);
        getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, null);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    private RedirectModalWindow createModalWindow(String id) {
        RedirectModalWindow modiaModalWindow = new RedirectModalWindow(id);
        modiaModalWindow.setInitialHeight(280);
        modiaModalWindow.setInitialWidth(600);
        modiaModalWindow.setContent(new SjekkForlateSide(modiaModalWindow.getContentId(), modiaModalWindow, this.answer));
        modiaModalWindow.setWindowClosedCallback(createWindowClosedCallback(modiaModalWindow));
        modiaModalWindow.setCloseButtonCallback(createCloseButtonCallback());
        return modiaModalWindow;
    }

    private void handleRedirect(AjaxRequestTarget target, PageParameters pageParameters, Class<? extends Page> redirectTo) {
        redirectPopup.setTarget(redirectTo, pageParameters);
        if (lamellContainer.hasUnsavedChanges()) {
            redirectPopup.show(target);
        } else {
            redirectPopup.redirect();
        }
    }

    private WindowClosedCallback createWindowClosedCallback(final RedirectModalWindow modalWindow) {
        return new WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget ajaxRequestTarget) {
                if (answer.is(DISCARD)) {
                    modalWindow.redirect();
                }
                ajaxRequestTarget.appendJavaScript(getJavascriptSaveButtonFocus());
            }
        };
    }

    private CloseButtonCallback createCloseButtonCallback() {
        return new CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget ajaxRequestTarget) {
                ajaxRequestTarget.appendJavaScript(getJavascriptSaveButtonFocus());
                return true;
            }
        };
    }

    private class NullstillLink extends AjaxLink<Void> {
        public NullstillLink(String id) {
            super(id);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            handleRedirect(target, new PageParameters(), HentPersonPage.class);
        }
    }

}
