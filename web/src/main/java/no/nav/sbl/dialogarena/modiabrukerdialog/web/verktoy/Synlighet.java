package no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy;

import org.apache.wicket.Component;

public final class Synlighet {

    public static Component[] skjulMenTaMedIMarkupLikevel(Component... components) {
        for (Component component : components) {
            component.setOutputMarkupPlaceholderTag(true);
            component.setVisibilityAllowed(false);
        }
        return components;
    }

    public static Component taMedIMarkupSelvOmUsynlig(Component component) {
        return component.setOutputMarkupPlaceholderTag(true);
    }

    public static Component flippSynlighet(Component component) {
        return component.setVisibilityAllowed(!component.isVisibilityAllowed());
    }

    public static Component skjul(Component component) {
        return component.setVisibilityAllowed(false);
    }

    public static Component vis(Component component) {
        return component.setVisibilityAllowed(true);
    }

}
