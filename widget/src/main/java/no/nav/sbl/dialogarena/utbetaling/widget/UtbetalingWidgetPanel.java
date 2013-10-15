package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.time.Datoformat;
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
        String utbetalingsDato = Optional.optional(utbetalingVM.getUtbetalingsDato()).map(Datoformat.KORT).getOrElse("Ingen utbetalingsdato");
        String startDato =Optional.optional(utbetalingVM.getStartDato()).map(Datoformat.KORT).getOrElse("Ingen startdato");
        String sluttDato =Optional.optional(utbetalingVM.getSluttDato()).map(Datoformat.KORT).getOrElse("Ingen sluttdato");

        String periode = startDato + "-" + sluttDato;

        statusContainer.add(new Label("status", utbetalingVM.getStatus()));
        add(statusContainer,
                new Label("utbetalingsDato",utbetalingsDato),
                new Label("belop", utbetalingVM.getBelop()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                new Label("periode", periode));
    }
}
