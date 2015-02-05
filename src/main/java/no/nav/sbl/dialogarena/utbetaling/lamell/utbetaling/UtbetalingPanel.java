package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import no.nav.sbl.dialogarena.utbetaling.util.VMUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        add(
                new DetaljPanel("detaljpanel", utbetalingVM),
                new WebMarkupContainer("mottakerIndikator")
                        .add(new AttributeAppender("class", utbetalingVM.getMottakertype()).setSeparator(" ")),
                new Label("kortPosteringDato", utbetalingVM.getKortPosteringsDato()),
                new Label("status", utbetalingVM.getStatus()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                getHovedYtelsesPeriodeLabel(utbetalingVM),
                new Label("bruttoBelopMedValuta", utbetalingVM.getBruttoBelop()),
                new Label("trekkMedValuta", utbetalingVM.getTrekk()),
                new Label("belopMedValuta", utbetalingVM.getBelop())
        );
    }

    private Label getHovedYtelsesPeriodeLabel(UtbetalingVM utbetalingVM) {
        if (VMUtils.erDefinertPeriode(utbetalingVM.getStartDato(), utbetalingVM.getSluttDato())) {
            return new Label("periodeMedKortDato", utbetalingVM.getPeriodeMedKortDato());
        }
        return (Label) new Label("periodeMedKortDato",
                new StringResourceModel("utbetaling.lamell.utbetaling.udefinertperiode", UtbetalingPanel.this, null).getString())
                .add(new AttributeAppender("class", "kursiv").setSeparator(" "));
    }

}
