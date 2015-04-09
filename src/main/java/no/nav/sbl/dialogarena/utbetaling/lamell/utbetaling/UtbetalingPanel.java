package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.components.PrintEkspanderContainer;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.erGyldigStartSluttVerdier;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        add(new DetaljPanel("detaljpanel", utbetalingVM),
                new PrintEkspanderContainer("printEkspander", UtbetalingPanel.this.getMarkupId()),
                new Label("utbetalingDato", utbetalingVM.getVisningsdatoFormatted()),
                createStatusLabel(utbetalingVM),
                new Label("ytelse", utbetalingVM.getYtelse()),
                new Label("belop", utbetalingVM.getUtbetalt()),
                getHovedYtelsesPeriodeLabel(utbetalingVM),
                forfallsdatoContainer(utbetalingVM),
                new Label("utbetaltTil", utbetalingVM.getMottakerNavn())
                );
    }

    private Label createStatusLabel(UtbetalingVM utbetalingVM) {
        Label statusLabel = new Label("status", utbetalingVM.getStatus());
        if(utbetalingVM.isUtbetalt()) {
            statusLabel.add(new AttributeAppender("class", "utbetalt").setSeparator(" "));
        }
        return statusLabel;
    }

    private WebMarkupContainer forfallsdatoContainer(UtbetalingVM utbetalingVM) {
        WebMarkupContainer container = new WebMarkupContainer("forfallsdatoContainer");
        container.add(
                new Label("forfallDato", utbetalingVM.getForfallsDatoFormatted()),
                new Label("forfallDatoLabel", new StringResourceModel("utbetaling.lamell.utbetaling.forfallsdato.label", this, null))
                ).setVisible(skalViseForfallsdato(utbetalingVM));
        return container;
    }

    private Label getHovedYtelsesPeriodeLabel(UtbetalingVM utbetalingVM) {
        if (erGyldigStartSluttVerdier(utbetalingVM.getStartDato(), utbetalingVM.getSluttDato())) {
            return new Label("periode", utbetalingVM.getPeriodeMedKortDato());
        }
        return (Label) new Label("periode",
                new StringResourceModel("utbetaling.lamell.utbetaling.udefinertperiode", UtbetalingPanel.this, null).getString())
                .add(new AttributeAppender("class", "kursiv").setSeparator(" "));
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
