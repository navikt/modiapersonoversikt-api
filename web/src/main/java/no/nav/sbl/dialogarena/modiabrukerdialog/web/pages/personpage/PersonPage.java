package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.eksternelenker.EksterneLenkerPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.AbstractTabPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.VisitkortTabListePanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.LamellPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.ReactSjekkForlatModal;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlernavnpanel.SaksbehandlernavnPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.timeout.ReactTimeoutBoksModal;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentCallback;
import org.apache.commons.collections15.Closure;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.FNR_CHANGED;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static no.nav.modig.modia.events.InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.LAMELL_LINK_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.PERSONSOK_FNR_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.modig.modia.lamell.ReactSjekkForlatModal.getJavascriptSaveButtonFocus;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.URL_TIL_SESSION_PARAMETERE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.LAMELL_MELDINGER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Denne klassen brukes til å vise informasjon om en bruker. Visningen består av lameller, widgets og paneler.
 */
public class PersonPage extends BasePage {

    private static final Logger logger = getLogger(PersonPage.class);

    public static final String VALGT_OPPGAVE_HENVENDELSEID_ATTR = "valgt-oppgave-henvendelseid";
    public static final String VALGT_OPPGAVE_ID_ATTR = "valgt-oppgave-id";
    public static final String VALGT_OPPGAVE_FNR_ATTR = "valgt-oppgave-fnr";
    public static final String ERROR = "error";
    public static final String SOKT_FNR = "soektfnr";
    public static final String FNR = "fnr";
    public static final String SIKKERHETSTILTAK = "sikkerhetstiltak";
    public static final ConditionalCssResource INTERN_IE = new ConditionalCssResource(new CssResourceReference(PersonPage.class, "personpage_ie9.css"), "screen", "lt IE 10");
    public static final PackageResourceReference DIALOGPANEL_LESS = new PackageResourceReference(HenvendelseVM.class, "DialogPanel.less");
    public static final ConditionalCssResource DIALOGPANEL_IE = new ConditionalCssResource(new CssResourceReference(DialogPanel.class, "DialogPanel_ie9.css"), "screen", "lt IE 10");
    public static final String PEN_SAKSBEH_ACTION = "pensaksbeh";

    private final String fnr;

    private LamellContainer lamellContainer;
    private ReactSjekkForlatModal redirectPopup;

    @Inject
    @Named("pep")
    private EnforcementPoint pep;

    public PersonPage(PageParameters pageParameters) {
        super(pageParameters);
        fnr = pageParameters.get("fnr").toString();

        if (pageParameters.getNamedKeys().size() > 1) {//FNR er alltid i url
            clearSession();
        }

        boolean parametereBleFunnetOgFlyttet = flyttURLParametereTilSession(pageParameters);
        if (parametereBleFunnetOgFlyttet) {
            setResponsePage(this.getClass(), pageParameters);
            return;
        }

        redirectPopup = new ReactSjekkForlatModal("redirectModal");
        konfigurerRedirectPopup();

        lamellContainer = new LamellContainer("lameller", fnr, getSession());

        SaksbehandlerInnstillingerPanel saksbehandlerInnstillingerPanel = new SaksbehandlerInnstillingerPanel("saksbehandlerInnstillingerPanel");
        final boolean hasPesysTilgang = pep.hasAccess(forRequest(actionId(PEN_SAKSBEH_ACTION), resourceId("")));
        add(
                new HentPersonPanel("searchPanel", false, pageParameters),
                new Button("toggle-sok"),
                new NullstillLink("nullstill"),
                lamellContainer,
                redirectPopup,
                saksbehandlerInnstillingerPanel,
                new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerInnstillingerToggler", saksbehandlerInnstillingerPanel.getMarkupId()),
                new PlukkOppgavePanel("plukkOppgave"),
                new SaksbehandlernavnPanel("saksbehandlerNavn"),
                new PersonsokPanel("personsokPanel").setVisible(true),
                new VisittkortPanel("visittkort", fnr).setVisible(true),
                new VisitkortTabListePanel("kjerneinfotabs", createTabs(), fnr, hasPesysTilgang),
                new DialogPanel("dialogPanel", fnr),
                new ReactTimeoutBoksModal("timeoutBoks", fnr)
        );

        if (isNotBlank((String) getSession().getAttribute(HENVENDELSEID))) {
            lamellContainer.setStartLamell(LAMELL_MELDINGER);
        }
    }

    private void clearSession() {
        on(URL_TIL_SESSION_PARAMETERE).forEach(new Closure<String>() {
            @Override
            public void execute(String param) {
                getSession().removeAttribute(param);
            }
        });
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        fjernURLParamatereFraSession();
    }

    private List<AbstractTab> createTabs() {
        List<AbstractTab> tabs = new ArrayList<>();
        tabs.add(new AbstractTabPanel("Familie") {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new PersonKjerneinfoPanel(panelId, fnr);
            }
        });
        tabs.add(new AbstractTabPanel("Lenker") {
            @Override
            public WebMarkupContainer getPanel(String panelId) {

                boolean hasAaregTilgang = pep.hasAccess(forRequest(actionId("aaregles"), resourceId("")));
                boolean hasPesysTilgang = pep.hasAccess(forRequest(actionId(PEN_SAKSBEH_ACTION), resourceId("")));
                return new EksterneLenkerPanel(panelId, fnr, hasAaregTilgang, hasPesysTilgang);
            }
        });

        return tabs;
    }

    private boolean flyttURLParametereTilSession(PageParameters pageParameters) {
        boolean fantParamVerdi = false;
        for (String param : URL_TIL_SESSION_PARAMETERE) {
            StringValue paramVerdi = pageParameters.get(param);
            if (!paramVerdi.isEmpty()) {
                getSession().setAttribute(param, paramVerdi.toString());
                pageParameters.remove(param, paramVerdi.toString());
                fantParamVerdi = true;
            }
        }
        return fantParamVerdi;
    }

    private void fjernURLParamatereFraSession() {
        for (String param : URL_TIL_SESSION_PARAMETERE) {
            getSession().setAttribute(param, null);
        }
    }

    private void konfigurerRedirectPopup() {
        redirectPopup.addCallback(ReactSjekkForlatModal.DISCARD, Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                redirectPopup.redirect();
            }
        });
        redirectPopup.addCallback(ReactSjekkForlatModal.CONFIRM, Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                redirectPopup.hide(target);
                target.appendJavaScript(getJavascriptSaveButtonFocus());
            }
        });
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, PageParameters pageParameters) {
        handleRedirect(target, pageParameters, PersonPage.class);
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, PageParameters pageParameters) {
        getSession().setAttribute(HENT_PERSON_BEGRUNNET, true);
        refreshKjerneinfo(target, pageParameters);
    }

    @RunOnEvents(GOTO_HENT_PERSONPAGE)
    public void gotoHentPersonPage(AjaxRequestTarget target, String query) throws JSONException {
        String errorText = getTextFromPayload(query, HentPersonPanel.JSON_ERROR_TEXT);
        String sikkerhetstiltak = getTextFromPayload(query, HentPersonPanel.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
        String soktFnr = getTextFromPayload(query, HentPersonPanel.JSON_SOKT_FNR);

        PageParameters pageParameters = new PageParameters();
        if (!StringUtils.isEmpty(sikkerhetstiltak)) {
            pageParameters.set(ERROR, errorText).set(SIKKERHETSTILTAK, sikkerhetstiltak).set(SOKT_FNR, soktFnr);
        } else {
            pageParameters.set(ERROR, errorText).set(SOKT_FNR, soktFnr);
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

    @RunOnEvents({Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT})
    public void slettPlukketOppgaveFraSession() {
        getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, null);
        getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, null);
        getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, null);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    private void handleRedirect(AjaxRequestTarget target, PageParameters pageParameters, Class<? extends Page> redirectTo) {
        redirectPopup.setTarget(redirectTo, pageParameters);
        if (lamellContainer.hasUnsavedChanges()) {
            redirectPopup.show(target);
        } else {
            redirectPopup.redirect();
        }
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

    /**
     * Hente forskjellige teksten fra en payload (JSONobjekt).
     *
     * @param query
     * @param jsonField
     * @return
     * @throws JSONException
     */
    protected String getTextFromPayload(String query, String jsonField) throws JSONException {
        return getJsonField(query, jsonField);
    }

    private String getJsonField(String query, String field) throws JSONException {
        JSONObject jsonObject = new JSONObject(query);
        if (jsonObject.has(field)) {
            return new JSONObject(query).getString(field);
        } else {
            return null;
        }
    }
}
