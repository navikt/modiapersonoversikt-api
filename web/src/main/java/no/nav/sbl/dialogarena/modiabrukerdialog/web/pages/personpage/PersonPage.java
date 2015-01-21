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
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSide;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.DialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.timeout.TimeoutBoks;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerTogglerPanel;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.constants.URLParametere.OPPGAVEID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.LAMELL_MELDINGER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow.getJavascriptSaveButtonFocus;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakePanel.LEGG_TILBAKE_UTFORT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.CloseButtonCallback;
import static org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Denne klassen brukes til å vise informasjon om en bruker. Visningen består av lameller, widgets og paneler.
 */
public class PersonPage extends BasePage {

    private static final Logger logger = getLogger(PersonPage.class);

    private static final List<String> URL_TIL_SESSION_PARAMETERE = asList(HENVENDELSEID, OPPGAVEID);

    public static final String VALGT_OPPGAVE_HENVENDELSEID_ATTR = "valgt-oppgave-henvendelseid";
    public static final String VALGT_OPPGAVE_ID_ATTR = "valgt-oppgave-id";
    public static final String VALGT_OPPGAVE_FNR_ATTR = "valgt-oppgave-fnr";
    public static final String ERROR = "error";
    public static final String SIKKERHETSTILTAK = "sikkerhetstiltak";
    public static final ConditionalCssResource INTERN_IE = new ConditionalCssResource(new CssResourceReference(PersonPage.class, "personpage_ie.css"), "screen", "lt IE 10");
    public static final PackageResourceReference SVAR_OG_REFERATPANEL_LESS = new PackageResourceReference(HenvendelseVM.class, "SvarOgReferatPanel.less");
    public static final JavaScriptResourceReference SELECTMENU_JS = new JavaScriptResourceReference(HenvendelseVM.class, "jquery-ui-selectmenu.min.js");

    private final String fnr;

    private LamellContainer lamellContainer;
    private RedirectModalWindow redirectPopup;

    public PersonPage(PageParameters pageParameters) {
        fnr = pageParameters.get("fnr").toString();
        boolean parametereBleFunnetOgFlyttet = flyttURLParametereTilSession(pageParameters);
        if (parametereBleFunnetOgFlyttet) {
            setResponsePage(this.getClass(), pageParameters);
            return;
        }

        redirectPopup = createRedirectModalWindow("redirectModal");
        lamellContainer = new LamellContainer("lameller", fnr);

        SaksbehandlerInnstillingerPanel saksbehandlerInnstillingerPanel = new SaksbehandlerInnstillingerPanel("saksbehandlerInnstillingerPanel");

        add(
                new HentPersonPanel("searchPanel"),
                new Button("toggle-sok"),
                new NullstillLink("nullstill"),
                lamellContainer,
                redirectPopup,
                saksbehandlerInnstillingerPanel,
                new SaksbehandlerInnstillingerTogglerPanel("saksbehandlerInnstillingerToggler", saksbehandlerInnstillingerPanel.getMarkupId()),
                new PlukkOppgavePanel("plukkOppgave"),
                new PersonsokPanel("personsokPanel").setVisible(true),
                new VisittkortPanel("visittkort", fnr).setVisible(true),
                new VisitkortTabListePanel("kjerneinfotabs", createTabs(), fnr),
                new DialogPanel("dialogPanel", fnr),
                new TimeoutBoks("timeoutBoks", fnr)
        );

        if (isNotBlank((String) getSession().getAttribute(HENVENDELSEID))) {
            lamellContainer.setStartLamell(LAMELL_MELDINGER);
        }
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        fjernURLParamatereFraSession();
    }

    private List<AbstractTab> createTabs() {
        List<AbstractTab> tabs = new ArrayList<>();
        tabs.add(new AbstractTabPanel(new Model<>("img/familie_ikon.svg"), "familie") {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new PersonKjerneinfoPanel(panelId, fnr);
            }
        });
        tabs.add(new AbstractTabPanel(new Model<>("svg/kjerneinfo/lenker_ikon.svg"), "lenker") {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new EksterneLenkerPanel(panelId, fnr);
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

    @RunOnEvents({MELDING_SENDT_TIL_BRUKER, LEGG_TILBAKE_UTFORT})
    public void slettPlukketOppgaveFraSession() {
        getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, null);
        getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, null);
        getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, null);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    private RedirectModalWindow createRedirectModalWindow(String id) {
        final SjekkForlateSideAnswer answer = new SjekkForlateSideAnswer();

        final RedirectModalWindow modiaModalWindow = new RedirectModalWindow(id);
        modiaModalWindow.setInitialHeight(280);
        modiaModalWindow.setInitialWidth(600);
        modiaModalWindow.setContent(new SjekkForlateSide(modiaModalWindow.getContentId(), modiaModalWindow, answer));
        modiaModalWindow.setWindowClosedCallback(new WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget ajaxRequestTarget) {
                if (answer.is(DISCARD)) {
                    modiaModalWindow.redirect();
                }
                ajaxRequestTarget.appendJavaScript(getJavascriptSaveButtonFocus());
            }
        });
        modiaModalWindow.setCloseButtonCallback(new CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget ajaxRequestTarget) {
                ajaxRequestTarget.appendJavaScript(getJavascriptSaveButtonFocus());
                return true;
            }
        });
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

    private class NullstillLink extends AjaxLink<Void> {
        public NullstillLink(String id) {
            super(id);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            handleRedirect(target, new PageParameters(), HentPersonPage.class);
        }
    }

    protected String getSikkerhetsTiltakBeskrivelse(String query) throws JSONException {
        return getJsonField(query, HentPersonPanel.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
    }

    protected String getErrorText(String query) throws JSONException {
        return getJsonField(query, HentPersonPanel.JSON_ERROR_TEXT);
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
