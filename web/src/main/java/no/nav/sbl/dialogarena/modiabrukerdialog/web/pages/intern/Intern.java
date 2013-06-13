package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;

public class Intern extends BasePage {

    private Logger logger = LoggerFactory.getLogger(Intern.class);
    @Inject
    private LamellHandler lamellHandler;


    public Intern(PageParameters pageParameters) {
        final String fnrFromRequest = pageParameters.get("fnr").toString(null);

        add(
                //                new HentPersonPanel("searchPanel"),
                //                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
                new PersonsokPanel("personsokPanel").setVisible(true),
                lamellHandler.createLamellPanel("lameller", fnrFromRequest),
                new SideBar("sideBar", fnrFromRequest).setVisible(true),
                new AjaxLink<Boolean>("nullstill") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        logger.info("-------------");
                        logger.info("Nullstill trykket");
                        throw new RestartResponseException(
                                HentPersonPage.class
                        );
                    }
                }

        );
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    //        @RunOnEvents(FODSELSNUMMER_FUNNET)
    //        public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
    //            throw new RestartResponseException(
    //                    Intern.class,
    //                    new PageParameters().set(FNR, query)
    //            );
    //        }

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
