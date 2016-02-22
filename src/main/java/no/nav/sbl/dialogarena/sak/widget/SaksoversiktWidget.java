package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;

import java.util.HashMap;

import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class SaksoversiktWidget extends Widget {

    public SaksoversiktWidget(String id, final String fnr) {
        super(id, "S", null);

        // Gjør header klikkbar
        header.add(
                createHeaderClickBehavior(),
                new AttributeAppender("class", "klikkbar-header").setSeparator(" "),
                new AttributeAppender("role", "link").setSeparator(" ")
        );

        // Bare listen er laget i react
        add(new ReactComponentPanel("saksoversiktWidget", "SaksoversiktWidget", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }}));
    }

    private AjaxEventBehavior createHeaderClickBehavior() {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                send(SaksoversiktWidget.this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload(SaksoversiktWidget.this.getId())));
            }
        };
    }
}
