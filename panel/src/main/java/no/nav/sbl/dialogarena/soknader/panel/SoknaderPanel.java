package no.nav.sbl.dialogarena.soknader.panel;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.markup.html.basic.Label;

public class SoknaderPanel extends Lerret {

    public SoknaderPanel(String id, String fnr){
        super(id);
        add(new Label("heading","Søknader"));

    }
}
