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
                new Label("periodeLabel", model.getObject().getPeriode()),
                new Label("antallUtbetalinger", model.getObject().getUtbetalinger().size()),
                new Label("utbetaltSum", model.getObject().getUtbetalt()),
                new Label("trekkSum", model.getObject().getTrekk()),
                new Label("bruttoSum", model.getObject().getBrutto())
        );
    }

    public void setOppsummering(OppsummeringProperties oppsummering) {
        setDefaultModelObject(oppsummering);
    }
}
