package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        add(
                new DetaljPanel("detaljpanel", utbetalingVM),
                new Label("statusBeskrivelse", utbetalingVM.getStatus()),
                new Label("kortUtbetalingsDato", utbetalingVM.getKortUtbetalingsDato()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                new Label("periodeMedKortDato", utbetalingVM.getPeriodeMedKortDato()),
                new Label("bruttoBelopMedValuta", utbetalingVM.getBruttoBelopMedValuta()),
                new Label("trekkMedValuta", utbetalingVM.getTrekkMedValuta()),
                new Label("belopMedValuta", utbetalingVM.getBelopMedValuta())
        );
    }

}
