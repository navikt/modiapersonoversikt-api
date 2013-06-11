package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;

import java.util.List;

import no.nav.dialogarena.modiabrukerdialog.example.component.ExamplePanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.LamellFactory;
import no.nav.modig.modia.lamell.LamellPanel;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.lamell.LerretFactory;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.GenericLerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.Oversikt;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class Intern extends BasePage {

    private static final String LAMELLER = "lameller";
    public static final String LAMELL_EXAMPLE = "example";
    public static final String LAMELL_OVERSIKT = "oversikt";


    public Intern(PageParameters pageParameters) {
        add(
                new TokenLamellPanel("lameller", createLamellFactories()),
                new SideBar("sideBar").setVisible(true)
        );
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        final LamellPanel lameller = (LamellPanel) get(LAMELLER);
        try{
            EventRouter.handleFeedItemEvent(lameller, event, feedItemPayload);
        }catch(ApplicationException e){
            target.appendJavaScript("alert('" + e.getMessage()  +"');");
        }
    }

    @RunOnEvents(WIDGET_LINK_CLICKED)
    public void widgetLinkClicked(AjaxRequestTarget target, String linkId) {
        final LamellPanel lameller = (LamellPanel) get(LAMELLER);
        try{
            EventRouter.handleWidgetItemEvent(lameller, linkId);
        }catch(ApplicationException e){
            target.appendJavaScript("alert('" + e.getMessage()  +"');");
        }

    }

    private List<LamellFactory> createLamellFactories() {
        return asList(
                newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new Oversikt(id);
                    }
                }),
                newLamellFactory(LAMELL_EXAMPLE, "", true, new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new GenericLerret(id, new ExamplePanel("panel"));
                    }
                })
        );
    }

}
