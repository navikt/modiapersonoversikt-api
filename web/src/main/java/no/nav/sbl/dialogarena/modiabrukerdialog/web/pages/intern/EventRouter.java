package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.dialogarena.modiabrukerdialog.example.component.ExamplePanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.LamellPanel;
import org.apache.wicket.event.IEvent;


public class EventRouter {

    public static void handleFeedItemEvent(LamellPanel lameller, IEvent<?> event, FeedItemPayload feedItemPayload) {
        final String type = feedItemPayload.getType();
        if (type.equals(ExamplePanel.EXAMPLE_TYPE)) {
            lameller.goToLamell(Intern.LAMELL_EXAMPLE);
            lameller.sendToLamell(Intern.LAMELL_EXAMPLE, event.getPayload());
            return;
        } else {
            throw new ApplicationException("Lenke med ukjent type <" + type + "> klikket");
        }
    }

    public static void handleWidgetItemEvent(LamellPanel lameller, String linkId) {
        throw new ApplicationException("Lenke med ukjent id <" + linkId + "> klikket');");
    }
}
