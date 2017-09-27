package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;


import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;

import static org.apache.wicket.event.Broadcast.BREADTH;

public class LeggTilbakeDelvisSvarPanel extends Panel {

    public static final String WICKET_REACT_PANEL_ID = "reactleggtilbakedelvissvarpanel";
    public static final String WICKET_REACT_WRAPPER_ID = "leggtilbakedelvissvarpanel";
    public static final String REACT_ID = "LeggTilbakeDelvisSvarPanel";
    public static final String SVAR_DELVIS_CALLBACK_ID = "delvisSvarSendt";

    private LeggTilbakeDelvisSvarProps leggTilbakeDelvisSvarProps;

    public LeggTilbakeDelvisSvarPanel(Melding sporsmal, String behandlingsId) {
        super(WICKET_REACT_WRAPPER_ID);
        setOutputMarkupPlaceholderTag(true);
        leggTilbakeDelvisSvarProps = new LeggTilbakeDelvisSvarProps(sporsmal, behandlingsId);

        add(lagReactPanel());
    }

    private Component lagReactPanel() {
        ReactComponentPanel reactComponentPanel = new ReactComponentPanel(WICKET_REACT_PANEL_ID, REACT_ID, leggTilbakeDelvisSvarProps.lagProps());
        reactComponentPanel.addCallback(SVAR_DELVIS_CALLBACK_ID, Void.class, (target, data) -> oppdaterMeldingerUI());
        reactComponentPanel
                .setOutputMarkupId(true)
                .setVisibilityAllowed(true);

        return reactComponentPanel;
    }

    private void oppdaterMeldingerUI() {
        send(getPage(), BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
    }

}
