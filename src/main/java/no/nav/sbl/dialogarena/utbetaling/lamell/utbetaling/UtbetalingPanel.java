package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        add(
                new DetaljPanel("detaljpanel", utbetalingVM),
                new WebMarkupContainer("mottakerIndikator")
                        .add(new AttributeAppender("class", utbetalingVM.getMottakertype()).setSeparator(" ")),
                new Label("kortUtbetalingsDato", utbetalingVM.getKortUtbetalingsDato()),
                new Label("status", utbetalingVM.getStatus()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                new Label("periodeMedKortDato", utbetalingVM.getPeriodeMedKortDato()),
                new Label("bruttoBelopMedValuta", utbetalingVM.getBruttoBelopMedValuta()),
                new Label("trekkMedValuta", utbetalingVM.getTrekkMedValuta()),
                new Label("belopMedValuta", utbetalingVM.getBelopMedValuta())
        );
    }

}
