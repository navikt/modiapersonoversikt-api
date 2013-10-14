package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;


public class NyesteMeldingPanel extends Panel {
    public NyesteMeldingPanel(String id) {
        super(id);
        add(new Label("nyesteMelding.opprettetDato"));
        add(new Label("nyesteMelding.avsender"));
        add(new MultiLineLabel("nyesteMelding.fritekst"));
    }
}
