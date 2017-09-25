package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;


import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.HashMap;
import java.util.Map;

public class LeggTilbakeDelvisSvarPanel extends Panel {

    public static final String WICKET_ID = "reactleggtilbakedelvissvarpanel";
    public static final String REACT_ID = "LeggTilbakeDelvisSvarPanel";

    private final String behandlingsId;

    public LeggTilbakeDelvisSvarPanel(String behandlingsId) {
        super("leggtilbakedelvissvarpanel");
        setOutputMarkupPlaceholderTag(true);

        this.behandlingsId = behandlingsId;

        add(lagReactPanel());
    }

    private Component lagReactPanel() {
        ReactComponentPanel reactComponentPanel = new ReactComponentPanel(WICKET_ID, REACT_ID, lagProps());
        reactComponentPanel
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);

        return reactComponentPanel;
    }

    private Map<String, Object> lagProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("henvendelseId", behandlingsId);
        return props;
    }

}
