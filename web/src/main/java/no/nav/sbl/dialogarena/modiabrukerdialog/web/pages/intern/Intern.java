package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
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

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;

public class Intern extends BasePage {

    private final SjekkForlateSideAnswer answer;
    @Inject
    private LamellHandler lamellHandler;

    public Intern(PageParameters pageParameters) {
        final String fnrFromRequest = pageParameters.get("fnr").toString(null);
        this.answer = new SjekkForlateSideAnswer();
        final ModigModalWindow modalWindow = createModalWindow("modal");
        add(
                new HentPersonPanel("searchPanel"),
                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
                new PersonsokPanel("personsokPanel").setVisible(true),
                lamellHandler.createLamellPanel("lameller", fnrFromRequest),
                new SideBar("sideBar", fnrFromRequest).setVisible(true),
                new AjaxLink<Boolean>("nullstill") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (lamellHandler.hasUnsavedChanges()) {
                            modalWindow.show(target);
                        } else {
                            gotoHentPersonPage();
                        }
                    }
                },
                modalWindow
        );
    }

    private void gotoHentPersonPage() {
        throw new RestartResponseException(
                HentPersonPage.class
        );
    }

    private ModigModalWindow createModalWindow(String id) {
        final ModigModalWindow modalWindow = new ModigModalWindow(id);
        modalWindow.setInitialHeight(150);
        modalWindow.setContent(new SjekkForlateSide(modalWindow.getContentId(), modalWindow, this.answer));
        modalWindow.setWindowClosedCallback(new ModigModalWindow.WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget ajaxRequestTarget) {
                if (answer.getAnswer().equals("OK")) {
                    gotoHentPersonPage();
                }
            }
        });
        modalWindow.setCloseButtonCallback(new ModigModalWindow.CloseButtonCallback() {
            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget ajaxRequestTarget) {
                // slik at man kan lukke vinduet med krysset i høyre hjørne.
                return true;
            }
        });
        return modalWindow;
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

            @RunOnEvents(HentPersonPanel.FODSELSNUMMER_FUNNET)
            public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
                throw new RestartResponseException(
                        Intern.class,
                        new PageParameters().set("fnr", query)
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
