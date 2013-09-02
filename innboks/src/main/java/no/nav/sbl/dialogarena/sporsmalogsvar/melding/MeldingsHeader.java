package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class MeldingsHeader extends Panel {

    private final Label overskrift;
    private final Label opprettetDato;

    public MeldingsHeader(String id) {
        super(id);
        overskrift = new Label("overskrift");
        add(overskrift);
        opprettetDato = new Label("opprettetDato");
        add(opprettetDato);
    }

    public Label getOverskrift() {
        return overskrift;
    }

    public Label getOpprettetDato() {
        return opprettetDato;
    }
}
