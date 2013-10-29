package no.nav.sbl.dialogarena.utbetaling.widget;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;

public class UtbetalingWidgetPanel extends GenericPanel<UtbetalingVM> {

    public UtbetalingWidgetPanel(String id, IModel<UtbetalingVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        UtbetalingVM utbetalingVM = model.getObject();

        add(
                createStatusContainer(),
                createUtbetalingsDatoLabel(utbetalingVM),
                new Label("belop", utbetalingVM.getBelop()),
                new Label("valuta", utbetalingVM.getValuta()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                createPeriodeLabel(utbetalingVM)
        );
    }

    private Label createPeriodeLabel(UtbetalingVM utbetalingVM) {
        return new Label(
                "periode",
                getStartDato(utbetalingVM) + " - " + getSluttDato(utbetalingVM));
    }

    private String getSluttDato(UtbetalingVM utbetalingVM) {
        StringResourceModel stringResourceModel = new StringResourceModel("sluttdato.mangler", this, new Model());
        return optional(utbetalingVM.getSluttDato()).map(KORT).getOrElse(stringResourceModel.getString());
    }

    private String getStartDato(UtbetalingVM utbetalingVM) {
        StringResourceModel stringResourceModel = new StringResourceModel("startdato.mangler", this, new Model());
        return optional(utbetalingVM.getStartDato()).map(KORT).getOrElse(stringResourceModel.getString());
    }

    private Label createUtbetalingsDatoLabel(UtbetalingVM utbetalingVM) {
        StringResourceModel stringResourceModel = new StringResourceModel("utbetalingdato.mangler", this, new Model());
        return new Label("utbetalingsDato", optional(utbetalingVM.getUtbetalingsDato()).map(KORT).getOrElse(stringResourceModel.getString()));
    }

    private WebMarkupContainer createStatusContainer() {
        return new WebMarkupContainer("status-container");
    }

}
