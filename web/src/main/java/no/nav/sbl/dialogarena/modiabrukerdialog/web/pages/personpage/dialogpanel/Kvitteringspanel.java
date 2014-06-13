package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;

public class Kvitteringspanel extends Panel {

    private String kvitteringsmelding;
    private AbstractAjaxTimerBehavior timeout;

    public Kvitteringspanel(String id) {
        super(id);
        setVisibilityAllowed(false);
        setOutputMarkupPlaceholderTag(true);
        add(new Label("kvitteringsmelding", new PropertyModel(this, "kvitteringsmelding")));
    }

    public void visISekunder(Duration tid, AjaxRequestTarget target, final Form<DialogVM> form) {
        setVisibilityAllowed(true);
        target.add(this);
        kvitteringsmelding = getString(form.getModelObject().kanal.getKvitteringKey());

        if (timeout == null) {
            timeout = new AbstractAjaxTimerBehavior(tid) {
                @Override
                protected void onTimer(AjaxRequestTarget target) {
                    Kvitteringspanel.this.setVisibilityAllowed(false);

                    form.setVisibilityAllowed(true);

                    target.add(getComponent());
                    target.add(form);
                    stop(target);
                    send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
                }
            };
            add(timeout);
        } else {
            timeout.restart(target);
        }
    }
}
