package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.markup.html.basic.Label;

public class BrukerhenvendelserPanel extends Lerret {
    public BrukerhenvendelserPanel(String id, String brukerident) {
        super(id);

        add(new Label("brukerFnr", brukerident));
    }
}