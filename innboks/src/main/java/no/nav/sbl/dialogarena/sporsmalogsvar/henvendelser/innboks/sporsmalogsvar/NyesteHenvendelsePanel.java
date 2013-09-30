package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.sporsmalogsvar;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;


public class NyesteHenvendelsePanel extends Panel {
    public NyesteHenvendelsePanel(String id) {
        super(id);
        add(new Label("nyesteHenvendelse.opprettetDato"));
        add(new Label("nyesteHenvendelse.henvendelse.overskrift"));
        add(new Label("nyesteHenvendelse.henvendelse.fritekst"));
    }
}
