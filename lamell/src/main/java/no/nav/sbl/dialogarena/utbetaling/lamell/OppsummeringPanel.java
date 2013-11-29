package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.lamell.filter.OppsummeringProperties;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class OppsummeringPanel extends Panel {

    public OppsummeringPanel(String id, IModel<OppsummeringProperties> model) {
        super(id, model);

        add(
                new Label("oppsummertPeriodeLabel", "Periode"),
                new Label("utbetaltLabel", "Utbetalt"),
                new Label("trekkLabel", "Trekk"),
                new Label("bruttoLabel", "Brutto"),
                new Label("oppsummertPeriode"),
                new Label("oppsummering.utbetalt"),
                new Label("oppsummering.trekk"),
                new Label("oppsummering.brutto")
        );
    }
}
