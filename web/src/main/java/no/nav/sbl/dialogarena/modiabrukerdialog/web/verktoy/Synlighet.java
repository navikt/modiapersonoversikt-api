package no.nav.sbl.dialogarena.modiabrukerdialog.web.verktoy;

import org.apache.wicket.Component;

public final class Synlighet {
    public static Component[] skjulMenTaMedIMarkupLikevel(Component... components) {
        for (Component c : components) {
            skjulMenTaMedIMarkupLikevel(c);
        }
        return components;
    }

    public static Component skjulMenTaMedIMarkupLikevel(Component component) {
        component.setOutputMarkupPlaceholderTag(true);
        component.setVisibilityAllowed(false);
        return component;
    }

    public static Component taMedIMarkupSelvOmUsynlig(Component component) {
        component.setOutputMarkupPlaceholderTag(true);
        return component;
    }

    public static Component flippSynlighet(Component component) {
        component.setVisibilityAllowed(!component.isVisibilityAllowed());
        return component;
    }

    public static Component skjul(Component component) {
        component.setVisibilityAllowed(false);
        return component;
    }

    public static Component vis(Component component) {
        component.setVisibilityAllowed(true);
        return component;
    }
}
