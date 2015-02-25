package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.erDefinertPeriode;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        add(new DetaljPanel("detaljpanel", utbetalingVM),
                createStatusPanel("statuspanel", utbetalingVM),
                new Label("ytelse", utbetalingVM.getYtelse()),
                new Label("utbetaltTil", utbetalingVM.getMottakerNavn()),
                getHovedYtelsesPeriodeLabel(utbetalingVM),
                new Label("belopMedValuta", utbetalingVM.getUtbetalt()));
    }

    private Label getHovedYtelsesPeriodeLabel(UtbetalingVM utbetalingVM) {
        if (erDefinertPeriode(utbetalingVM.getStartDato(), utbetalingVM.getSluttDato())) {
            return new Label("periodeMedKortDato", utbetalingVM.getPeriodeMedKortDato());
        }
        return (Label) new Label("periodeMedKortDato",
                new StringResourceModel("utbetaling.lamell.utbetaling.udefinertperiode", UtbetalingPanel.this, null).getString())
                .add(new AttributeAppender("class", "kursiv").setSeparator(" "));
    }

    protected WebMarkupContainer createStatusPanel(String id, UtbetalingVM utbetalingVM) {
        WebMarkupContainer container = new WebMarkupContainer(id);

        String statusText = utbetalingVM.getStatus();
        if(skalViseForfallsdato(utbetalingVM)) {
            statusText += ", ";
        }

        container.add(
                new Label("utbetalingDato", utbetalingVM.getVisningsdatoFormatted()),
                new Label("status", statusText),
                new Label("forfallDatoLabel", new StringResourceModel("utbetaling.lamell.utbetaling.forfallsdato.label", this, null))
                    .setVisible(skalViseForfallsdato(utbetalingVM)),
                new Label("forfallDato", utbetalingVM.getForfallsDatoFormatted())
                    .setVisible(skalViseForfallsdato(utbetalingVM))
        );

        return container;
    }

    /**
     * Viser forfallsdato hvis:
     * a. Ikke utbetalt
     * b. Har forfallsdato
     *
     * @param utbetalingVM
     * @return
     */
    protected boolean skalViseForfallsdato(UtbetalingVM utbetalingVM) {
        if(!isUtbetalt(utbetalingVM) && hasForfallsdato(utbetalingVM)) {
            return true;
        }
        return false;
    }

    protected boolean isUtbetalt(UtbetalingVM utbetalingVM) {
        return utbetalingVM.getUtbetalingDato() != null;
    }

    protected boolean hasForfallsdato(UtbetalingVM utbetalingVM) {
        return utbetalingVM.getForfallsDato() != null;
    }

}
