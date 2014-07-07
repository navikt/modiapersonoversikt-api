package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;

public class KvitteringsPanel extends Panel {

    private String kvitteringsmelding;
    private AbstractAjaxTimerBehavior timeout;

    public KvitteringsPanel(String id) {
        super(id);
        setVisibilityAllowed(false);
        setOutputMarkupPlaceholderTag(true);
        add(new Label("kvitteringsmelding", new PropertyModel(this, "kvitteringsmelding")));
    }

    public void visISekunder(Duration tid, AjaxRequestTarget target, String kvitteringsmelding, final Component... components) {
        this.kvitteringsmelding = kvitteringsmelding;
        for (Component component : components) {
            component.setVisibilityAllowed(false);
        }
        this.setVisibilityAllowed(true);
        target.add(this);

        if (timeout == null) {
            timeout = new AbstractAjaxTimerBehavior(tid) {
                @Override
                protected void onTimer(AjaxRequestTarget target) {
                    KvitteringsPanel.this.setVisibilityAllowed(false);
                    for (Component component : components) {
                        component.setVisibilityAllowed(true);
                    }
                    target.add(getComponent());
                    target.add(components);
                    stop(target);
                    send(getPage(), Broadcast.BREADTH, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
                }
            };
            add(timeout);
        } else {
            timeout.restart(target);
        }
    }
}
