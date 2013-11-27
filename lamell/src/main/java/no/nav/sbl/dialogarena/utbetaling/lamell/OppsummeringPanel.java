package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.logikk.Oppsummerer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class OppsummeringPanel extends Panel {


    public OppsummeringPanel(String id, Periode periode) {
        super(id);

        Oppsummering oppsummering = new Oppsummerer().lagOppsummering(periode);

        add(
                new Label("utbetaltLabel", "Totalt utbetalt"),
                new Label("trekkLabel", "Totalt trekk"),
                new Label("bruttoLabel", "Totalt brutto"),
                new Label("periodeLabel", periode.getPeriode()),
                new Label("utbetaltSum", oppsummering.utbetalt),
                new Label("trekkSum", oppsummering.trekk),
                new Label("bruttoSum", oppsummering.brutto)
        );
    }

}
