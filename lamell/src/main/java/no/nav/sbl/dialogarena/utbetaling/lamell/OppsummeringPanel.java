package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.lamell.filter.OppsummeringProperties;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class OppsummeringPanel extends Panel {

    public OppsummeringPanel(String id, IModel<OppsummeringProperties> model) {
        super(id, model);

        add(
                new Label("utbetaltLabel", "Totalt utbetalt"),
                new Label("trekkLabel", "Totalt trekk"),
                new Label("bruttoLabel", "Totalt brutto"),
                new Label("oppsummertPeriodeLabel"),
                new Label("antallUtbetalinger"),
                new Label("oppsummering.utbetalt"),
                new Label("oppsummering.trekk"),
                new Label("oppsummering.brutto")
        );
    }
}
