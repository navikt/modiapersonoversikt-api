package no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;

public class SporsmalOgSvarPanel extends Panel {

    public SporsmalOgSvarPanel(String id) {
        super(id);
        setOutputMarkupId(true);
        add(new AttributeAppender("class", "besvare-panel"));

        final BesvareSporsmalPanel besvare = new BesvareSporsmalPanel("besvare-sporsmal");
        add(
                besvare,
                new AjaxLink<Void>("brukerhenvendelser-link") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(InternalEvents.FEED_ITEM_CLICKED, new FeedItemPayload(null, null, "brukerhenvendelser")));
                    }
                });
    }

    @SuppressWarnings("unused")
    @RunOnEvents(BesvareSporsmalPanel.SPORSMAL_OPPDATERT)
    public void sporsmalAvbrutt(AjaxRequestTarget target) {
        target.add(this);
    }


}
