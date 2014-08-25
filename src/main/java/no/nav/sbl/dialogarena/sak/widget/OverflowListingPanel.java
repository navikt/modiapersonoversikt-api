package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.panels.GenericListingPanel;
import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;

import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;

public class OverflowListingPanel extends GenericListingPanel {

    public OverflowListingPanel(String message, final SaksoversiktWidget widget) {
        add(new Label("message", message));
        add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                send(widget, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_LINK_CLICKED, new OverflowPayload()));
            }
        });
    }
}
