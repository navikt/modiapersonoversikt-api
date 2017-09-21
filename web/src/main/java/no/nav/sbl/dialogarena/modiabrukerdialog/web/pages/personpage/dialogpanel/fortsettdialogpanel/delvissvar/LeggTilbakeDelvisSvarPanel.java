package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;


import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

public class LeggTilbakeDelvisSvarPanel extends Panel {

    public LeggTilbakeDelvisSvarPanel() {
        super("leggtilbakedelvissvarpanel");
        setOutputMarkupPlaceholderTag(true);
        add(lagReactPanel());
    }

    private Component lagReactPanel() {
        ReactComponentPanel reactComponentPanel = new ReactComponentPanel("reactleggtilbakedelvissvarpanel", "LeggTilbakeDelvisSvarPanel");
        reactComponentPanel
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);

        return reactComponentPanel;
    }

}
