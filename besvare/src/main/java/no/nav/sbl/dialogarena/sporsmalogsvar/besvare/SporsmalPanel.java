package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class SporsmalPanel extends Panel {

    public SporsmalPanel(String id, BesvareModell model) {
        super(id, model);
        add(
                new Label("sporsmal.opprettetDato"),
                new Label("sporsmal.overskrift"),
                new Label("sporsmal.fritekst"));
    }
}
