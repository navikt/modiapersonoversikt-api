package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AnimertPanel extends Panel {

    public AnimertPanel(String id) {
        super(id);
    }

    public void togglePanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            target.prependJavaScript("lukket|$('#" + this.getMarkupId() + "').slideUp(lukket)");
            this.setVisibilityAllowed(false);
            target.add(this);
        } else {
            target.appendJavaScript("$('#" + this.getMarkupId() + "').slideDown()");
            this.setVisibilityAllowed(true);
            target.add(this);
        }
    }

    public void lukkPanel(AjaxRequestTarget target) {
        if (isVisibilityAllowed()) {
            target.prependJavaScript("lukket|$('#" + this.getMarkupId() + "').slideUp(lukket)");
            this.setVisibilityAllowed(false);
            target.add(this);
        }
    }
}
