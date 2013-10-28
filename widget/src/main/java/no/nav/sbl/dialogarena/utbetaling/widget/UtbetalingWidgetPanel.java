package no.nav.sbl.dialogarena.utbetaling.widget;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;

public class UtbetalingWidgetPanel extends GenericPanel<UtbetalingVM> {

    public UtbetalingWidgetPanel(String id, IModel<UtbetalingVM> model) {
        super(id, model);
        setOutputMarkupId(true);

        add(
                createStatusContainer(model.getObject()),
                createUtbetalingsDatoLabel(model.getObject()),
                new Label("belop", model.getObject().getBelop()),
                new Label("beskrivelse", model.getObject().getBeskrivelse()),
                createPeriodeLabel(model.getObject())
        );
    }

    private Label createPeriodeLabel(UtbetalingVM utbetalingVM) {
        return new Label(
                "periode",
                getStartDato(utbetalingVM) + " - " + getSluttDato(utbetalingVM));
    }

    private String getSluttDato(UtbetalingVM utbetalingVM) {
        return optional(utbetalingVM.getSluttDato()).map(KORT).getOrElse("Ingen sluttdato");
    }

    private String getStartDato(UtbetalingVM utbetalingVM) {
        return optional(utbetalingVM.getStartDato()).map(KORT).getOrElse("Ingen startdato");
    }

    private Label createUtbetalingsDatoLabel(UtbetalingVM utbetalingVM) {
        return new Label("utbetalingsDato", optional(utbetalingVM.getUtbetalingsDato()).map(KORT).getOrElse("Ingen utbetalingsdato"));
    }

    private WebMarkupContainer createStatusContainer(UtbetalingVM utbetalingVM) {
        return (WebMarkupContainer) new WebMarkupContainer("status-container").add(new Label("status", utbetalingVM.getStatus()));
    }

}
