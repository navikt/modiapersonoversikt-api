package no.nav.sbl.dialogarena.modiabrukerdialog.sak.widget;

import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.widget.Widget;
import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.link.Link;

import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class SaksoversiktWidget extends Widget<Object> {

    public SaksoversiktWidget(String id) {
        super(id, "S", null);

        // Gjør header klikkbar
        header.add(
                createHeaderClickBehavior(),
                new AttributeAppender("class", "klikkbar-header").setSeparator(" "),
                new AttributeAppender("role", "link").setSeparator(" ")
        );


        add(
                new Link("vissaker") {
                    @Override
                    public void onClick() {}
                }.add(createHeaderClickBehavior())
        );
    }

    public final void apneSaksoversiktLamell() {
        send(this, Broadcast.BUBBLE, new NamedEventPayload(WIDGET_HEADER_CLICKED, new WidgetHeaderPayload(this.getId())));
    }

    //Should be lambda. Denne kan ikke bli det.
    @SuppressWarnings("squid:S1604")
    private AjaxEventBehavior createHeaderClickBehavior() {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                apneSaksoversiktLamell();
            }
        };
    }
}
