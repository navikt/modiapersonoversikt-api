package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;

import java.util.HashMap;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class SaksoversiktWidget extends Widget<Object> {

    public SaksoversiktWidget(String id, final String fnr) {
        super(id, "S", null);

        // Gjør header klikkbar
        header.add(
                createHeaderClickBehavior(),
                new AttributeAppender("class", "klikkbar-header").setSeparator(" "),
                new AttributeAppender("role", "link").setSeparator(" ")
        );

        // Bare listen er laget i react
        ReactComponentPanel feeditemListe = new ReactComponentPanel("saksoversiktWidget", "SaksoversiktWidget", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }});

        feeditemListe.addCallback("ITEM_CLICK", String.class, (target, data) -> {
            send(this, Broadcast.BUBBLE, new NamedEventPayload(
                    FEED_ITEM_CLICKED,
                    new FeedItemPayload(this.getId(), data, "tema"))
            );
        });

        feeditemListe.addCallback("VIS_ALLE_CLICK", Void.class, (target, data) -> {
            apneSaksoversiktLamell();
        });

        add(feeditemListe);
    }

    public final void apneSaksoversiktLamell() {
        send(this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload(this.getId())));
    }

    private AjaxEventBehavior createHeaderClickBehavior() {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                apneSaksoversiktLamell();
            }
        };
    }
}
