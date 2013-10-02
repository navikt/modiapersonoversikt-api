package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;


public class NyesteHenvendelsePanel extends Panel {
    public NyesteHenvendelsePanel(String id) {
        super(id);
        add(new Label("nyesteMelding.opprettetDato"));
        add(new Label("nyesteMelding.avsender"));
        add(new Label("nyesteMelding.fritekst"));
    }
}
