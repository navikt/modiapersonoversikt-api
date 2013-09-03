package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class MeldingsHeader extends Panel {

    private final Label overskrift;
    private final Label opprettetDato;

    public MeldingsHeader(String id, IModel model) {
        super(id, model);
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
