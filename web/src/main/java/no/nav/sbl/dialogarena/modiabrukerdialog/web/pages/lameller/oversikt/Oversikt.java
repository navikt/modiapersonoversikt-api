package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt;

import no.nav.dialogarena.modiabrukerdialog.example.component.ExampleWidget;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.widget.Widget.EVENT_UPDATE_WIDGET;
import static org.apache.wicket.event.Broadcast.BREADTH;
import static org.apache.wicket.util.time.Duration.minutes;


public class Oversikt extends Lerret {

    private final AbstractAjaxTimerBehavior timer;

    public Oversikt(String id) {

        super(id);
        add(
                new SykepengerWidget("sykepenger", "Y", new Model<>("12345123456")),
                new LenkeWidget("lenker", "E", new ListModel<>(asList("kontrakter"))),
                new ExampleWidget("example", "X")
        );

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
