package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.time.Duration;

public class Kvitteringspanel extends Panel {

    private AbstractAjaxTimerBehavior timeout;

    public Kvitteringspanel(String id) {
        super(id);
        setVisibilityAllowed(false);
        setOutputMarkupPlaceholderTag(true);
    }

    public void visISekunder(Duration tid, AjaxRequestTarget target, final Component ... skjulteKomponenter) {
        setVisibilityAllowed(true);
        target.add(this);

        if (timeout == null) {
            timeout = new AbstractAjaxTimerBehavior(tid) {
                @Override
                protected void onTimer(AjaxRequestTarget target) {
                    Kvitteringspanel.this.setVisibilityAllowed(false);

                    for (Component component : skjulteKomponenter) {
                        component.setVisibilityAllowed(true);
                    }

                    target.add(getComponent());
                    target.add(skjulteKomponenter);
                    stop(target);
                }
            };
            add(timeout);
        } else {
            timeout.restart(target);
        }
    }
}
