package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.frontend.ConditionalJavascriptResource;
import no.nav.modig.modia.constants.ModiaConstants;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.ModiaModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSide;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.image.ContextPathGenerator;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.FNR_CHANGED;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE;
import static no.nav.modig.modia.events.InternalEvents.FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.HENTPERSON_FODSELSNUMMER_IKKE_TILGANG;
import static no.nav.modig.modia.events.InternalEvents.PERSONSOK_FNR_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.ModiaModalWindow.getJavascriptSaveButtonFocus;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;

/**
 * Denne klassen brukes til å vise informasjon om en bruker. Instansiering skjer implisitt via events.
 */
public class Intern extends BasePage {

    private static final ResourceReference MEDIA_QUERIES = new PackageResourceReference(Intern.class, "respond.min.js");
    public static final ConditionalJavascriptResource RESPOND_JS = new ConditionalJavascriptResource(MEDIA_QUERIES, "lt IE 9");
    private final SjekkForlateSideAnswer answer;
    private final ModiaModalWindow modalWindow;

	private LamellHandler lamellHandler;

    public Intern(PageParameters pageParameters) {
        this.answer = new SjekkForlateSideAnswer();
        this.modalWindow = createModalWindow("modal");
        lamellHandler = new LamellHandler();
        instantiateComponents(pageParameters.get("fnr").toString(null));
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("fnr", query);
        handleNewFnrFoundEvent(target, pageParameters, Intern.class);
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, String query) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.set("fnr", query);
        getSession().setAttribute(ModiaConstants.HENT_PERSON_BEGRUNNET, true);
        handleNewFnrFoundEvent(target, pageParameters, Intern.class);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        try {
            lamellHandler.handleFeedItemEvent(event, feedItemPayload);
        } catch (ApplicationException e) {
            target.appendJavaScript("alert('" + e.getMessage() + "');");
        }
    }

    @RunOnEvents(WIDGET_LINK_CLICKED)
    public void widgetLinkClicked(AjaxRequestTarget target, String linkId) {
        try {
            lamellHandler.handleWidgetItemEvent(linkId);
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

	private void instantiateComponents(String fnrFromRequest) {
        add(
		        new Button("toggle-sok"),
		        new HentPersonPanel("searchPanel"),
		        new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
		        new PersonsokPanel("personsokPanel").setVisible(true),
		        lamellHandler.createLamellPanel("lameller", fnrFromRequest),
		        new SideBar("sideBar", fnrFromRequest).setVisible(true),
		        createNullstillLink(modalWindow),
		        modalWindow
        );
    }

    private AjaxLink<Boolean> createNullstillLink(final ModiaModalWindow modalWindow) {
        return new AjaxLink<Boolean>("nullstill") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (lamellHandler.hasUnsavedChanges()) {
                    modalWindow.show(target);
                } else {
                    modalWindow.redirect();
                }
            }
        };
    }

    private ModiaModalWindow createModalWindow(String id) {
        final ModiaModalWindow modiaModalWindow = new ModiaModalWindow(id);
        modiaModalWindow.setInitialHeight(260);
        modiaModalWindow.setInitialWidth(600);
        modiaModalWindow.setContent(new SjekkForlateSide(modiaModalWindow.getContentId(), modiaModalWindow, this.answer));
        modiaModalWindow.setWindowClosedCallback(createWindowClosedCallback(modiaModalWindow));
        modiaModalWindow.setCloseButtonCallback(createCloseButtonCallback());
        return modiaModalWindow;
    }

    private void handleNewFnrFoundEvent(AjaxRequestTarget target, PageParameters pageParameters, Class clazz) {
        modalWindow.setRedirectClass(clazz);
        modalWindow.setPageParameters(pageParameters);
        if (lamellHandler.hasUnsavedChanges()) {
            modalWindow.show(target);
        } else {
            modalWindow.redirect();
        }
    }

    private ModigModalWindow.WindowClosedCallback createWindowClosedCallback(final ModiaModalWindow modalWindow) {
        return new ModigModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget ajaxRequestTarget) {
                if (answer.getAnswerType() == DISCARD) {
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

}
