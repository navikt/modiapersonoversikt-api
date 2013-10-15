package no.nav.sbl.dialogarena.utbetaling.widget;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class UtbetalingWidgetPanel extends GenericPanel<UtbetalingVM> {

    public UtbetalingWidgetPanel(String id, IModel<UtbetalingVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        WebMarkupContainer statusContainer = new WebMarkupContainer("status-container");
        UtbetalingVM utbetalingVM = model.getObject();
        Label utbetalingsDato = new Label("utbetalingsDato", utbetalingVM.getUtbetalingsDato());
        Label beskrivelse = new Label("beskrivelse", utbetalingVM.getBeskrivelse());
        Label periode = new Label("periode", utbetalingVM.getPeriode());
        Label belop = new Label("belop", utbetalingVM.getBelop());
        Label status = new Label("status", utbetalingVM.getStatus());
        statusContainer.add(status);

        add(statusContainer, utbetalingsDato, belop, beskrivelse, periode);

    }
}
