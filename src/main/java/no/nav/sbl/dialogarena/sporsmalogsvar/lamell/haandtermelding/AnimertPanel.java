package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import static java.lang.String.format;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingValgPanel.PANEL_LUKKET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingValgPanel.PANEL_TOGGLET;
import static org.apache.wicket.event.Broadcast.BREADTH;

public abstract class AnimertPanel extends Panel {

    public static final String DEFAULT_SLIDE_DURATION = "400";
    private final boolean focusFirstElementOnOpen;

    public AnimertPanel(String id) {
        this(id, false);
    }

    public AnimertPanel(String id, boolean focusFirstElementOnOpen) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(false);
        this.focusFirstElementOnOpen = focusFirstElementOnOpen;
    }

    public void togglePanel(AjaxRequestTarget target) {
        togglePanel(target, DEFAULT_SLIDE_DURATION);
    }

    public void togglePanel(AjaxRequestTarget target, String duration) {
        if (isVisibilityAllowed()) {
            target.prependJavaScript(closeScript(duration));
            this.setVisibilityAllowed(false);
            onClose();
        } else {
            target.appendJavaScript(openScript(duration));
            this.setVisibilityAllowed(true);
            onOpen();
        }
        target.add(this);
    }

    private String openScript(String duration) {
        if (focusFirstElementOnOpen) {
            return format("$('#%s').slideDown(%s, function(){$(this).find(':focusable:first').focus();});", this.getMarkupId(), duration);
        } else {
            return format("$('#%s').slideDown(%s);", this.getMarkupId(), duration);
        }
    }

    private String closeScript(String duration) {
        return format("lukket|$('#%s').slideUp(%s, lukket);", this.getMarkupId(), duration);
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

    protected void onOpen () {
    }

    protected void onClose() {
    }
}
