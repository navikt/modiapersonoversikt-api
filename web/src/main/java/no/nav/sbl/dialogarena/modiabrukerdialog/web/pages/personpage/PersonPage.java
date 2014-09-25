package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.eksternelenker.EksterneLenkerPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.AbstractTabPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.VisitkortTabListePanel;
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
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.OppgavetilordningFeilet;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.FNR_CHANGED;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static no.nav.modig.modia.events.InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.LAMELL_LINK_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.modia.events.InternalEvents.PERSONSOK_FNR_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService.FikkIkkeTilordnet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.LAMELL_MELDINGER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.LAMELL_OVERSIKT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow.getJavascriptSaveButtonFocus;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.KvitteringsPanel.KVITTERING_VIST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel.LEGG_TILBAKE_UTFORT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel.SVAR_AVBRUTT;
import static org.apache.commons.lang3.StringUtils.isBlank;
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
	public static final String SIKKERHETSTILTAK = "sikkerhetstiltak";
	public static final String ERROR = "error";

    @Inject
    protected HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    protected OppgaveBehandlingService oppgaveBehandlingService;

    private SjekkForlateSideAnswer answer;
    private RedirectModalWindow redirectPopup;
    private OppgavetilordningFeilet oppgavetilordningFeiletPopup;
    private LamellContainer lamellContainer;
    private HentPersonPanel hentPersonPanel;
    private Button searchToggleButton;
    private NullstillLink nullstillLink;
    private Component svarOgReferatPanel;
	private VisitkortTabListePanel visitkortTabListePanel;
    protected String startLamell = LAMELL_OVERSIKT;

    public PersonPage(PageParameters pageParameters) {
        fnr = pageParameters.get("fnr").toString(null);
        boolean parametereBleFunnetOgFlyttet = flyttUrlParametereTilSession(pageParameters, HENVENDELSEID, OPPGAVEID);
        if (parametereBleFunnetOgFlyttet) {
            setResponsePage(this.getClass(), pageParameters);
            return;
        }

        instansierFelter();
        add(
                hentPersonPanel,
                searchToggleButton,
                nullstillLink,
                lamellContainer,
                redirectPopup,
                oppgavetilordningFeiletPopup,
                new SaksbehandlerInnstillingerPanel("saksbehandlerInnstillingerPanel"),
                new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerInnstillingerToggler"),
                new PlukkOppgavePanel("plukkOppgave"),
                new PersonsokPanel("personsokPanel").setVisible(true),
                new VisittkortPanel("visittkort", fnr).setVisible(true),
				visitkortTabListePanel,
                svarOgReferatPanel,
                new TimeoutBoks("timeoutBoks", fnr)
        );

        settOppRiktigPanelOgLamell();
    }

    private void instansierFelter() {
        answer = new SjekkForlateSideAnswer();
        redirectPopup = createRedirectModalWindow("redirectModal");
        oppgavetilordningFeiletPopup = new OppgavetilordningFeilet("oppgavetilordningModal");
        lamellContainer = new LamellContainer("lameller", fnr) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                String lamell = PersonPage.this.startLamell;
                if (!lamell.equals(LAMELL_OVERSIKT)) {
                    goToLamell(lamell);
                }
            }
        };
        hentPersonPanel = (HentPersonPanel) new HentPersonPanel("searchPanel").setOutputMarkupPlaceholderTag(true);
        searchToggleButton = (Button) new Button("toggle-sok").setOutputMarkupPlaceholderTag(true);
        nullstillLink = (NullstillLink) new NullstillLink("nullstill").setOutputMarkupPlaceholderTag(true);
        svarOgReferatPanel = new ReferatPanel(SVAR_OG_REFERAT_PANEL_ID, fnr);

		visitkortTabListePanel = new VisitkortTabListePanel("kjerneinfotabs", createTabs());

    }

	private List createTabs() {
		List tabs = new ArrayList();
		tabs.add(new AbstractTabPanel(new Model("img/familie_ikon.svg"), "familie") {
			@Override
			public WebMarkupContainer getPanel(String panelId ) {
				return new PersonKjerneinfoPanel(panelId, fnr);
			}
		});
		tabs.add(new AbstractTabPanel(new Model("svg/kjerneinfo/lenker_ikon.svg"), "lenker") {
			@Override
			public WebMarkupContainer getPanel(String panelId ) {
				return new EksterneLenkerPanel(panelId, fnr);
			}
		});

		return tabs;
	}

	private boolean flyttUrlParametereTilSession(PageParameters pageParameters, String... params) {
        boolean fantParamVerdi = false;
        for (String param : params) {
            StringValue paramVerdi = pageParameters.get(param);
            if (!paramVerdi.isEmpty()) {
                getSession().setAttribute(param, paramVerdi.toString());
                pageParameters.remove(param, paramVerdi.toString());
                fantParamVerdi = true;
            }
        }
        return fantParamVerdi;
    }

    private void settOppRiktigPanelOgLamell() {
        String henvendelseId = (String) getSession().getAttribute(HENVENDELSEID);
        String oppgaveId = (String) getSession().getAttribute(OPPGAVEID);

        if (isBlank(henvendelseId) && isNotBlank(oppgaveId)) {
            visSvarPanelBasertPaaOppgaveIdForSporsmal(oppgaveId);
        } else if (isNotBlank(henvendelseId) && isBlank(oppgaveId)) {
            startLamell = LAMELL_MELDINGER;
        } else if (isNotBlank(henvendelseId) && isNotBlank(oppgaveId)) {
            visSvarPanelBasertPaaHenvendelsesId(henvendelseId, oppgaveId);
            startLamell = LAMELL_MELDINGER;
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
    public void gotoHentPersonPage(AjaxRequestTarget target, String query) throws JSONException {
		String errorText = getErrorText(query);
		String sikkerhetstiltak = getSikkerhetsTiltakBeskrivelse(query);

		PageParameters pageParameters = new PageParameters();
		if (!StringUtils.isEmpty(sikkerhetstiltak)) {
			pageParameters.set(ERROR, errorText).set(SIKKERHETSTILTAK, sikkerhetstiltak);
		} else {
			pageParameters.set(ERROR, errorText);
		}

		throw new RestartResponseException(HentPersonPage.class, pageParameters);
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

    private void visSvarPanelBasertPaaOppgaveIdForSporsmal(String oppgaveId) {
        getSession().setAttribute(OPPGAVEID, null);
        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmalFromOppgaveId(fnr, oppgaveId);
        erstattReferatPanelMedSvarPanel(sporsmal, henvendelseUtsendingService.getSvarEllerReferatForSporsmal(fnr, sporsmal.id), optional(oppgaveId));
    }

    private void visSvarPanelBasertPaaHenvendelsesId(String henvendelseId, String oppgaveId) {
        getSession().setAttribute(OPPGAVEID, null);
        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmal(henvendelseId);
        erstattReferatPanelMedSvarPanel(sporsmal, henvendelseUtsendingService.getSvarEllerReferatForSporsmal(fnr, henvendelseId), optional(oppgaveId));
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void visSvarPanelBasertPaaSporsmalId(AjaxRequestTarget target, String sporsmalId) {
        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmal(sporsmalId);
        List<SvarEllerReferat> svar = henvendelseUtsendingService.getSvarEllerReferatForSporsmal(fnr, sporsmalId);
        Optional<String> oppgaveId = none();
        if (sporsmaletIkkeErBesvartTidligere(svar)) {
            try {
                oppgaveBehandlingService.tilordneOppgaveIGsak(sporsmal.oppgaveId);
                oppgaveId = optional(sporsmal.oppgaveId);
            } catch (FikkIkkeTilordnet fikkIkkeTilordnet) {
                oppgavetilordningFeiletPopup.vis(target);
            }
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

    @RunOnEvents({KVITTERING_VIST, LEGG_TILBAKE_UTFORT, SVAR_AVBRUTT})
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

    private RedirectModalWindow createRedirectModalWindow(String id) {
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

    public static Boolean isNotBlank(String s) {
        return !isBlank(s);
    }

	protected String getSikkerhetsTiltakBeskrivelse(String query) throws JSONException {
		return getJsonField(query, HentPersonPanel.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
	}

	protected String getErrorText(String query) throws JSONException {
		return getJsonField(query, HentPersonPanel.JSON_ERROR_TEXT);
	}

	private String getJsonField(String query, String field) throws JSONException {
		JSONObject jsonObject =  new JSONObject(query);
		if (jsonObject.has(field)) {
			return new JSONObject(query).getString(field);
		} else {
			return null;
		}
	}
}
