package no.nav.sbl.dialogarena.modiabrukerdialog.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;

public class AnimasjonsUtils {

    public static void animertVisningToggle(AjaxRequestTarget target, Component component) {
        if (component.isVisibilityAllowed()) {
            target.prependJavaScript("lukket|$('#" + component.getMarkupId() + "').slideUp(lukket)");
            component.setVisibilityAllowed(false);
        } else {
            component.add(new DisplayNoneBehavior());
            target.appendJavaScript("$('#" + component.getMarkupId() + "').slideDown()");
            component.setVisibilityAllowed(true);
        }
    }

    private static class DisplayNoneBehavior extends AttributeAppender {
        public DisplayNoneBehavior() {
            super("style", "display:none");
        }

        @Override
        public boolean isTemporary(Component component) {
            return true;
        }
    }
}
