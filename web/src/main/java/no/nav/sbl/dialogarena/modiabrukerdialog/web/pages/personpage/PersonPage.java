package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.visittkort.VisittkortPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.frontend.ConditionalJavascriptResource;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.LamellContainer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSide;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.timeout.TimeoutBoks;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;

import static no.nav.modig.modia.constants.ModiaConstants.HENT_PERSON_BEGRUNNET;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.FNR_CHANGED;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.GOTO_HENT_PERSONPAGE;
import static no.nav.modig.modia.events.InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.PERSONSOK_FNR_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.RedirectModalWindow.getJavascriptSaveButtonFocus;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Denne klassen brukes til å vise informasjon om en bruker. Visningen består av lameller, widgets og paneler.
 */
public class PersonPage extends BasePage {

    private Logger logger = getLogger(PersonPage.class);

    public static final ConditionalJavascriptResource RESPOND_JS = new ConditionalJavascriptResource(new PackageResourceReference(PersonPage.class, "respond.min.js"), "lt IE 9");
	public static final ConditionalCssResource INTERN_IE = new ConditionalCssResource(new CssResourceReference(PersonPage.class, "personpage_ie.css"), "screen", "lt IE 10");

	private SjekkForlateSideAnswer answer;
    private RedirectModalWindow redirectPopup;
	private LamellContainer lamellContainer;
    private HentPersonPanel hentPersonPanel;
    private Button searchToggleButton;
    private NullstillLink nullstillLink;

    public PersonPage(PageParameters pageParameters) {
        String fnr = pageParameters.get("fnr").toString(null);
        instansierFelter(fnr);
        add(
            hentPersonPanel,
            searchToggleButton,
            nullstillLink,
            new PersonsokPanel("personsokPanel").setVisible(true),
                lamellContainer,
		    new VisittkortPanel("visittkort", fnr).setVisible(true),
		    new PersonKjerneinfoPanel("personKjerneinfoPanel", fnr).setVisible(true),
            new TimeoutBoks("timeoutBoks", fnr),
            redirectPopup
        );
    }

    private void instansierFelter(String fnr) {
        answer = new SjekkForlateSideAnswer();
        redirectPopup = createModalWindow("modal");
        lamellContainer = new LamellContainer("lameller", fnr);
        hentPersonPanel = (HentPersonPanel) new HentPersonPanel("searchPanel").setOutputMarkupPlaceholderTag(true);
        searchToggleButton = (Button) new Button("toggle-sok").setOutputMarkupPlaceholderTag(true);
        nullstillLink = (NullstillLink) new NullstillLink("nullstill").setOutputMarkupPlaceholderTag(true);
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
            logger.warn("Burde ikke skje, klarte ikke håndtere feeditemevent: " + e.getMessage(), e);
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

    @RunOnEvents(WIDGET_LINK_CLICKED)
    public void widgetLinkClicked(AjaxRequestTarget target, String linkId) {
        try {
            lamellContainer.handleWidgetItemEvent(linkId);
        } catch (ApplicationException e) {
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

	@RunOnEvents(PERSONSOK_FNR_CLICKED)
	public void personsokresultatClicked(AjaxRequestTarget target, String query) {
		send(getPage(), Broadcast.DEPTH, new NamedEventPayload(FNR_CHANGED, query));
	}

	@RunOnEvents(HENTPERSON_FODSELSNUMMER_IKKE_TILGANG)
	public void personsokIkkeTilgang(AjaxRequestTarget target, String query) {
		send(getPage(), Broadcast.BREADTH, new NamedEventPayload(FODSELSNUMMER_IKKE_TILGANG, query));
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

    private ModigModalWindow.WindowClosedCallback createWindowClosedCallback(final RedirectModalWindow modalWindow) {
        return new ModigModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget ajaxRequestTarget) {
                if (answer.is(DISCARD)) {
                    modalWindow.redirect();
                }
                ajaxRequestTarget.appendJavaScript(getJavascriptSaveButtonFocus());
            }
        };
    }

    private ModigModalWindow.CloseButtonCallback createCloseButtonCallback() {
        return new ModigModalWindow.CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget ajaxRequestTarget) {
                ajaxRequestTarget.appendJavaScript(getJavascriptSaveButtonFocus());
                return true;
            }
        };
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

}
