package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;


public class NyesteMeldingPanel extends Panel {
    public NyesteMeldingPanel(String id) {
        super(id);
        add(new Label("nyesteMelding.opprettetDato"));
        add(new Label("nyesteMelding.avsender"));
        add(new URLParsingMultiLineLabel("nyesteMelding.melding.fritekst"));
    }
}
