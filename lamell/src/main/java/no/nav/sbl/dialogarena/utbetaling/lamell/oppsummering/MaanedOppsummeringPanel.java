package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class MaanedOppsummeringPanel extends Panel {

    public MaanedOppsummeringPanel(String id, OppsummeringVM oppsummeringVM) {
        super(id, new CompoundPropertyModel<>(oppsummeringVM));

        add(new Label("oppsummertPeriode"));
        add(new Label("brutto"));
        add(new Label("trekk"));
        add(new Label("utbetalt"));
    }
}
