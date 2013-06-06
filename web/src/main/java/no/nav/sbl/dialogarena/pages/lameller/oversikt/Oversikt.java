package no.nav.sbl.dialogarena.pages.lameller.oversikt;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.widget.LenkeWidget;
import no.nav.modig.modia.widget.Widget;
import no.nav.sykmeldingsperioder.widget.SykepengerWidget;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;

import static java.util.Arrays.asList;


public class Oversikt extends Lerret {

    private final AbstractAjaxTimerBehavior timer;

    public Oversikt(String id) {

        super(id);
        add(
                //                new TestFeedWidget("logg", "L"),
                //                new TestFeedWidget("soknad", "S"),
                //                new TestFeedWidget("dialog", "D"),
                //                new TestFeedWidget("sak", "K"),
                new SykepengerWidget("sykepenger", "Y", new Model<>("12345123456")),
                new LenkeWidget("lenker", "E", new ListModel<>(asList("saker", "lenkea", "lenkeb")))
        );

        timer = new AbstractAjaxTimerBehavior(Duration.minutes(30)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                send(Oversikt.this, Broadcast.BREADTH, Widget.EVENT_UPDATE_WIDGET);
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
