package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

public class LeggTilbakeDelvisSvarPanel extends Panel {

    public static final String WICKET_REACT_PANEL_ID = "reactleggtilbakedelvissvarpanel";
    public static final String WICKET_REACT_WRAPPER_ID = "leggtilbakedelvissvarpanel";
    public static final String REACT_ID = "LeggTilbakeDelvisSvarPanel";

    private LeggTilbakeDelvisSvarProps leggTilbakeDelvisSvarProps;

    public LeggTilbakeDelvisSvarPanel(Melding sporsmal, String behandlingsId) {
        super(WICKET_REACT_WRAPPER_ID);
        setOutputMarkupPlaceholderTag(true);
        leggTilbakeDelvisSvarProps = new LeggTilbakeDelvisSvarProps(sporsmal, behandlingsId);

        add(lagReactPanel());
    }

    private Component lagReactPanel() {
        ReactComponentPanel reactComponentPanel = new ReactComponentPanel(WICKET_REACT_PANEL_ID, REACT_ID, leggTilbakeDelvisSvarProps.lagProps());
        reactComponentPanel
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);

        return reactComponentPanel;
    }

}
