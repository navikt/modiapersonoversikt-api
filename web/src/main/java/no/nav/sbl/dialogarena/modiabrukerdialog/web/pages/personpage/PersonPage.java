package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.RecoverableAuthorizationException;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.AbstractTabPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.tab.VisitkortTabListePanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.LamellPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.ReactSjekkForlatModal;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.visittkort.navkontor.NavKontorPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.Hode;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.begrunnelse.ReactBegrunnelseModal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.SokOppBrukerCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.hode.jscallback.VoidCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.timeout.ReactTimeoutBoksModal;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.*;
import static no.nav.modig.modia.lamell.ReactSjekkForlatModal.getJavascriptSaveButtonFocus;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.LamellContainer.LAMELL_MELDINGER;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.event.Broadcast.DEPTH;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Denne klassen brukes til å vise informasjon om en bruker. Visningen består av lameller, widgets og paneler.
 */
public class PersonPage extends BasePage {

    private static final Logger logger = getLogger(PersonPage.class);

    public static final String ERROR = "error";
    public static final String SOKT_FNR = "soektfnr";
    public static final String SIKKERHETSTILTAK = "sikkerhetstiltak";
    public static final ConditionalCssResource INTERN_IE = new ConditionalCssResource(new CssResourceReference(PersonPage.class, "personpage_ie9.css"), "screen", "lt IE 10");
    public static final PackageResourceReference DIALOGPANEL_LESS = new PackageResourceReference(HenvendelseVM.class, "DialogPanel.less");
    public static final ConditionalCssResource DIALOGPANEL_IE = new ConditionalCssResource(new CssResourceReference(DialogPanel.class, "DialogPanel_ie9.css"), "screen", "lt IE 10");
    public static final String PEN_SAKSBEH_ACTION = "pensaksbeh";

    private final String fnr;
    private final GrunnInfo grunnInfo;
    private final DialogPanel dialogPanel;

    private LamellContainer lamellContainer;
    private ReactSjekkForlatModal redirectPopup;
    private ReactBegrunnelseModal oppgiBegrunnelseModal = null;

    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    @Inject
    private GrunninfoService grunninfoService;
    @Inject
    private UnleashService unleashService;

    public PersonPage(PageParameters pageParameters) {
        super(pageParameters);
        if (pageParameters.get("fnr").isEmpty()) {
            fnr = hentFodselsnummerFraRequest().orElseThrow(() -> new RestartResponseException(HentPersonPage.class, new PageParameters()));
        } else {
            fnr = pageParameters.get("fnr").toString();
        }
        DialogSession session = DialogSession.read(this);
        sjekkTilgang(fnr, pageParameters);
        grunnInfo = grunninfoService.hentGrunninfo(fnr);
        boolean skalViseMeldingerLamell = session.oppgaverBlePlukket() || erRequestFraGosys(pageParameters);

        if (oppgaverPaSessionTilhorerAnnetFNREnnFraUrl(session)) {
            session.clearOppgaveSomBesvaresOgOppgaveFraUrl();
        }
        if (erRequestFraGosys(pageParameters)) {
            session.withURLParametre(pageParameters);
        }
        pageParameters.remove(OPPGAVEID, HENVENDELSEID, BESVARES);

        redirectPopup = new ReactSjekkForlatModal("redirectModal");
        konfigurerRedirectPopup();

        boolean nyttVisittkortEnabled = unleashService.isEnabled(Feature.NYTT_VISITTKORT);
        boolean nyBrukerprofilEnabled = unleashService.isEnabled(Feature.NY_BRUKERPROFIL);
        boolean nySaksoversikt = unleashService.isEnabled(Feature.NY_SAKSOVERSIKT);
        lamellContainer = new LamellContainer("lameller", getSession(), grunnInfo, nyBrukerprofilEnabled, nySaksoversikt);

        oppgiBegrunnelseModal = new ReactBegrunnelseModal("oppgiBegrunnelseModal");
        Hode hode = new Hode("hode", oppgiBegrunnelseModal, personKjerneinfoServiceBi, grunnInfo, null);
        hode.addCallback("fjernperson", new VoidCallback((target, component) -> {
            clearSession();
            handleRedirect(target, new PageParameters(), HentPersonPage.class);
        }));
        hode.add(hasCssClassIf("nytt-visittkort-toggle", Model.of(nyttVisittkortEnabled)));
        hode.add(hasCssClassIf("ny-saksoversikt-toggle", Model.of(nySaksoversikt)));

        dialogPanel = new DialogPanel("dialogPanel", grunnInfo);
        add(
                hode,
                lamellContainer,
                redirectPopup,
                new PlukkOppgavePanel("plukkOppgaver"),
                new PersonsokPanel("personsokPanel").setVisible(true),
                dialogPanel,
                new ReactTimeoutBoksModal("timeoutBoks", fnr),
                oppgiBegrunnelseModal
        );

        add(getVisittkortkomponenter(nyBrukerprofilEnabled, nyttVisittkortEnabled));

        if (skalViseMeldingerLamell) {
            lamellContainer.setStartLamell(LAMELL_MELDINGER);
        }
        HentPersonPage.configureModalWindow(oppgiBegrunnelseModal, pageParameters);
    }

    @NotNull
    private Component[] getVisittkortkomponenter(boolean nyBrukerprofilEnabled, boolean nyttVisittkortEnabled) {
        if (nyttVisittkortEnabled) {
            return new Component[]{
                    new WebMarkupContainer("visittkort").setVisible(false),
                    new WebMarkupContainer("brukersNavKontor").setVisible(false),
                    new WebMarkupContainer("kjerneinfotabs").setVisible(false),
                    new ReactComponentPanel("nytt-visittkort", "NyttVisittkort", new HashMap<String, Object>() {{
                        put("fødselsnummer", fnr);
                        put("nyBrukerprofil", nyBrukerprofilEnabled);
                    }})
            };
        } else {
            return new Component[]{
                    new VisittkortPanel("visittkort", fnr).setVisible(true),
                    new NavKontorPanel("brukersNavKontor", fnr),
                    new VisitkortTabListePanel("kjerneinfotabs", createTabs()),
                    new WebMarkupContainer("nytt-visittkort").setVisible(false)
            };
        }
    }

    private boolean oppgaverPaSessionTilhorerAnnetFNREnnFraUrl(DialogSession session) {
        return session.getOppgaveSomBesvares().map(oppgave -> !fnr.equals(oppgave.fnr)).orElse(false)
                || ofNullable(session.getOppgaveFraUrl()).map(oppgave -> !fnr.equals(oppgave.fnr)).orElse(false);
    }

    private boolean erRequestFraGosys(PageParameters pageParameters) {
        return (!pageParameters.get(OPPGAVEID).isEmpty() || !pageParameters.get(HENVENDELSEID).isEmpty())
                && pageParameters.get(BESVARES).isEmpty();
    }

    private Optional<String> hentFodselsnummerFraRequest() {
        List<String> segments = RequestCycle.get().getRequest().getUrl().getSegments();
        if ("person".equals(segments.get(0))) {
            return ofNullable(segments.get(1));
        }
        return empty();
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
    }

    private List<AbstractTab> createTabs() {
        List<AbstractTab> tabs = new ArrayList<>();
        tabs.add(new AbstractTabPanel("Familie") {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new PersonKjerneinfoPanel(panelId, fnr);
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
                redirectPopup.hide();
                target.appendJavaScript(getJavascriptSaveButtonFocus());
            }
        });
    }

    private void sjekkTilgang(String fnr, PageParameters params) {
        HentKjerneinformasjonRequest request = new HentKjerneinformasjonRequest(fnr);
        String fnrBegrunnet = (String) getSession().getAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET);
        Boolean erBegrunnet = !isBlank(fnrBegrunnet) && fnrBegrunnet.equals(fnr);
        request.setBegrunnet((erBegrunnet == null) ? false : erBegrunnet);
        try {
            personKjerneinfoServiceBi.hentKjerneinformasjon(request);
        } catch (RecoverableAuthorizationException e) {
            throw new RestartResponseException(HentPersonPage.class, params);
        }
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, PageParameters pageParameters) {
        clearSession();
        handleRedirect(target, pageParameters, PersonPage.class);
    }

    private void clearSession() {
        DialogSession.read(this).clearOppgaveSomBesvaresOgOppgaveFraUrl();
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, PageParameters pageParameters) {
        getSession().setAttribute(HENT_PERSON_BEGRUNNET, pageParameters.get("fnr").toString());
        refreshKjerneinfo(target, pageParameters);
    }

    @RunOnEvents(GOTO_HENT_PERSONPAGE)
    public void gotoHentPersonPage(AjaxRequestTarget target, String query) throws JSONException {
        String errorText = getTextFromPayload(query, SokOppBrukerCallback.JSON_ERROR_TEXT);
        String sikkerhetstiltak = getTextFromPayload(query, SokOppBrukerCallback.JSON_SIKKERHETTILTAKS_BESKRIVELSE);
        String soktFnr = getTextFromPayload(query, SokOppBrukerCallback.JSON_SOKT_FNR);

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
        createEvent("hendelse.feeditem.klikk." + feedItemPayload.getType().toLowerCase()).report();
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
        createEvent("hendelse.widgetheader.klikk." + widgetHeaderPayload.getType().toLowerCase()).report();
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
    public void slettBesvartEllerTilbakelagtOppgaveFraSession() {
        DialogSession.read(this).withOppgaveSomBesvares(null);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    private void handleRedirect(AjaxRequestTarget target, PageParameters pageParameters, Class<? extends Page> redirectTo) {
        redirectPopup.setTarget(redirectTo, pageParameters);
        if (lamellContainer.hasUnsavedChanges()) {
            redirectPopup.show();
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
            clearSession();
            handleRedirect(target, new PageParameters(), HentPersonPage.class);
        }
    }

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
