package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT;

public class SaksbehandlerInnstillingerTogglerPanel extends Panel {

    public static final String SAKSBEHANDLERINNSTILLINGER_TOGGLET = "saksbehandlerinnstillinger.togglet";
    public static final JavaScriptResourceReference SAKSBEHANDLER_INNSTILLINGER_JS = new JavaScriptResourceReference(SaksbehandlerInnstillingerTogglerPanel.class, "SaksbehandlerInnstillinger.js");

    private boolean ekspandert = false;

    public SaksbehandlerInnstillingerTogglerPanel(String id, String panelId) {
        super(id);
        setOutputMarkupId(true);

        PropertyModel<Boolean> ekspandertModel = new PropertyModel<>(this, "ekspandert");
        WebMarkupContainer toggleWrapper = new WebMarkupContainer("toggleWrapper");
        add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                send(getPage(), Broadcast.DEPTH, SAKSBEHANDLERINNSTILLINGER_TOGGLET);
                togglePil(target);
            }
        });
        toggleWrapper.add(AttributeAppender.append("aria-controls", panelId));

        toggleWrapper.add(new ContextImage("togglebilde", "img/modiaLogo.svg"));
        toggleWrapper.add(new WebMarkupContainer("togglepil")
                .add(hasCssClassIf("ned", ekspandertModel))
                .add(hasCssClassIf("opp", not(ekspandertModel))));


        add(toggleWrapper);
    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_VALGT)
    private void togglePil(AjaxRequestTarget target) {
        ekspandert = !ekspandert;
        target.add(this);
    }
}
