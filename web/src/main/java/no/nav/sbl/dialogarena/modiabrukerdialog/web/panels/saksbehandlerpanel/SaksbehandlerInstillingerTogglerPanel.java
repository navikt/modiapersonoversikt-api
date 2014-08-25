package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINSTILLINGER_VALGT;

public class SaksbehandlerInstillingerTogglerPanel extends Panel {

    public static final String SAKSBEHANDLERINSTILLINGER_TOGGLET = "saksbehandlerinstillinger.togglet";

    private boolean ekspandert = false;

    public SaksbehandlerInstillingerTogglerPanel(String id) {
        super(id);

        setOutputMarkupId(true);

        add(new ContextImage("togglebilde", "img/modiaLogo.svg"));
        add(new WebMarkupContainer("togglepil")
                .add(hasCssClassIf("ned", new PropertyModel<Boolean>(this, "ekspandert")))
                .add(hasCssClassIf("opp", not(new PropertyModel<Boolean>(this, "ekspandert")))));

        add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                send(getPage(), Broadcast.DEPTH, SAKSBEHANDLERINSTILLINGER_TOGGLET);
                togglePil(target);
            }
        });
    }

    @RunOnEvents(SAKSBEHANDLERINSTILLINGER_VALGT)
    private void togglePil(AjaxRequestTarget target) {
        ekspandert = !ekspandert;
        target.add(this);
    }
}
