package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.Widget;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;

import java.util.HashMap;

public class SaksoversiktWidget extends Widget {

    public SaksoversiktWidget(String id, final String fnr) {
        super(id, "S", null);

        add(new ReactComponentPanel("saksoversiktWidget", "SaksoversiktWidget", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }}));
    }
}
