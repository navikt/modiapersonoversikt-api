package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import static java.lang.String.format;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_LUKKET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_TOGGLET;
import static org.apache.wicket.event.Broadcast.BREADTH;

public abstract class AnimertPanel extends Panel {

    public static final String DEFAULT_SLIDE_DURATION = "400";

    public AnimertPanel(String id) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(false);
    }

    public void togglePanel(AjaxRequestTarget target) {
        togglePanel(target, DEFAULT_SLIDE_DURATION);
    }

    public void togglePanel(AjaxRequestTarget target, String duration) {
        if (isVisibilityAllowed()) {
            target.prependJavaScript(format("lukket|$('#%s').slideUp(" + duration + ", lukket)", this.getMarkupId()));
            this.setVisibilityAllowed(false);
        } else {
            target.appendJavaScript(format("$('#%s').slideDown(" + duration + ")", this.getMarkupId()));
            this.setVisibilityAllowed(true);
        }
        target.add(this);
    }

    @RunOnEvents(PANEL_TOGGLET)
    public void haandterKlikk(AjaxRequestTarget target, Class<?> toggletPanel) {
        if (this.getClass().equals(toggletPanel)) {
            togglePanel(target);
        } else {
            lukkPanel(target);
        }
    }

    @RunOnEvents(MELDING_VALGT)
    public void lukkPanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            this.setVisibilityAllowed(false);
            send(getParent(), BREADTH, PANEL_LUKKET);
            target.prependJavaScript(format("lukket|$('#%s').slideUp(" + DEFAULT_SLIDE_DURATION + ", lukket)", this.getMarkupId()));
            target.add(this);
        }
    }
}
