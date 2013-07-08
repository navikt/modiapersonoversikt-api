package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.frontend.ConditionalJavascriptResource;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSide;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.modal.SjekkForlateSideAnswer.AnswerType.DISCARD;

/**
 *  Denne klassen brukes til å vise informasjon om en bruker. Instansiering skjer implisitt via events.
 */
public class Intern extends BasePage {

    private static final ResourceReference MEDIA_QUERIES = new PackageResourceReference(Intern.class, "respond.min.js");
    public static final ConditionalJavascriptResource RESPOND_JS = new ConditionalJavascriptResource(MEDIA_QUERIES, "lt IE 9");
    public static final JavaScriptResourceReference JS_SNURR = new JavaScriptResourceReference(Intern.class, "snurr.js");
    private static final String BEGRUNNELSE = "begrunnelse";
    private final SjekkForlateSideAnswer answer;

    @Inject
    private LamellHandler lamellHandler;

    public Intern(PageParameters pageParameters) {
        this.answer = new SjekkForlateSideAnswer();
        if (erBegrunnet(pageParameters)) {
            instantiateComponentsWithBegrunnelse(pageParameters.get("fnr").toString(null), true, createModalWindow("modal"));
        } else {
            instantiateComponentsWithoutBegrunnelse(pageParameters.get("fnr").toString(null), createModalWindow("modal"));
        }
    }

    private boolean erBegrunnet(PageParameters pageParameters) {
        return pageParameters.get(BEGRUNNELSE).toBoolean(false);
    }

    private void instantiateComponentsWithoutBegrunnelse(String fnrFromRequest, ModigModalWindow modalWindow) {
        add(
                new HentPersonPanel("searchPanel"),
                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
                new PersonsokPanel("personsokPanel").setVisible(true),
                lamellHandler.createLamellPanel("lameller", fnrFromRequest),
                new SideBar("sideBar", fnrFromRequest).setVisible(true),
                createNullstillLink(modalWindow),
                modalWindow
        );
    }

    private void instantiateComponentsWithBegrunnelse(String fnrFromRequest, boolean begrunnelse, ModigModalWindow modalWindow) {
        add(
                new HentPersonPanel("searchPanel", begrunnelse),
                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
                new PersonsokPanel("personsokPanel").setVisible(true),
                lamellHandler.createLamellPanel("lameller", fnrFromRequest),
                new SideBar("sideBar", fnrFromRequest).setVisible(true),
                createNullstillLink(modalWindow),
                modalWindow
        );
    }

    private AjaxLink<Boolean> createNullstillLink(final ModigModalWindow modalWindow) {
        return new AjaxLink<Boolean>("nullstill") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (lamellHandler.hasUnsavedChanges()) {
                    modalWindow.show(target);
                } else {
                    gotoHentPersonPage();
                }
            }
        };
    }

    private void gotoHentPersonPage() {
        throw new RestartResponseException(
                HentPersonPage.class
        );
    }

    private ModigModalWindow createModalWindow(String id) {
        final ModigModalWindow modalWindow = new ModigModalWindow(id);
        modalWindow.setInitialHeight(150);
        modalWindow.setInitialWidth(800);
        modalWindow.setContent(new SjekkForlateSide(modalWindow.getContentId(), modalWindow, this.answer));
        modalWindow.setWindowClosedCallback(new ModigModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget ajaxRequestTarget) {
                if (answer.getAnswerType() == DISCARD) {
                    gotoHentPersonPage();
                }
            }
        });
        modalWindow.setCloseButtonCallback(new ModigModalWindow.CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget ajaxRequestTarget) {
                return true;
            }
        });
        return modalWindow;
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(InternalEvents.FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(
                Intern.class,
                new PageParameters().set("fnr", query)
        );
    }

    @RunOnEvents(InternalEvents.FODSELSNUMMER_FUNNET_MED_BEGRUNNElSE)
    public void refreshKjerneinfoMedBegrunnelse(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(
                Intern.class,
                new PageParameters().set("fnr", query).set("begrunnelse", true)
        );
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

}
