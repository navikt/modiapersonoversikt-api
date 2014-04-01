package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.markup.html.basic.Label;

public class SaksoversiktLerret extends Lerret {
    public SaksoversiktLerret(String id, String fnr) {
        super(id);


        add(new Label("saksoversikt.fnr", fnr));
    }
}
