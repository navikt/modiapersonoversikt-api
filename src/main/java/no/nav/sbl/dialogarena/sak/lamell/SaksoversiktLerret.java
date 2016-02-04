package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;

import java.util.HashMap;


public class SaksoversiktLerret extends Lerret {
    public SaksoversiktLerret(String id, final String fnr) {
        super(id);

        add(new ReactComponentPanel("saksoversiktLerret", "SaksoversiktLerret", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }}));
    }
}

