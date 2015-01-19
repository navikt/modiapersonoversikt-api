package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import static java.lang.String.format;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_LUKKET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel.PANEL_TOGGLET;
import static org.apache.wicket.event.Broadcast.BREADTH;

public abstract class AnimertPanel extends Panel {

    public AnimertPanel(String id) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(false);
    }

    public void togglePanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            target.prependJavaScript(format("lukket|$('#%s').slideUp(lukket)", this.getMarkupId()));
            this.setVisibilityAllowed(false);
        } else {
            target.appendJavaScript(format("$('#%s').slideDown()", this.getMarkupId()));
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
    
    @RunOnEvents(VALGT_MELDING_EVENT)
    public void lukkPanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            this.setVisibilityAllowed(false);
            send(getParent(), BREADTH, PANEL_LUKKET);
            target.prependJavaScript(format("lukket|$('#%s').slideUp(lukket)", this.getMarkupId()));
            target.add(this);
        }
    }
}
