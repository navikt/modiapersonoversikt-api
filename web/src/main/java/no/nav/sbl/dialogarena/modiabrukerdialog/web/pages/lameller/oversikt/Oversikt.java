package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import static no.nav.modig.modia.widget.Widget.EVENT_UPDATE_WIDGET;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.util.time.Duration.minutes;
import no.nav.dialogarena.modiabrukerdialog.example.component.ExampleWidget;
import no.nav.modig.modia.lamell.Lerret;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;


public class Oversikt extends Lerret {

    private final AbstractAjaxTimerBehavior timer;

    public Oversikt(String id) {

        super(id);
        add(
        // new SykepengerWidget("sykepenger", "Y", new Model<>("12345123456")),
        // new LenkeWidget("lenker", "E", new ListModel<>(asList("saker",
        // "lenkea", "lenkeb"))),
        new ExampleWidget("example", "X"));

        timer = new AbstractAjaxTimerBehavior(minutes(30)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                send(Oversikt.this, BREADTH, EVENT_UPDATE_WIDGET);
            }
        };
        add(timer);
    }

    @Override
    public void onClosing(AjaxRequestTarget target) {
        timer.stop(target);
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        timer.restart(target);
    }

}
