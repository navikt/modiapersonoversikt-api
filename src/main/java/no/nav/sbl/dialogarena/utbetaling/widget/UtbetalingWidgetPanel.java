package no.nav.sbl.dialogarena.utbetaling.widget;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.time.Datoformat.kortUtenLiteral;

public class UtbetalingWidgetPanel extends GenericPanel<UtbetalingVM> {

    public UtbetalingWidgetPanel(String id, IModel<UtbetalingVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        UtbetalingVM utbetalingVM = model.getObject();

        add(
                createUtbetalingsDatoLabel(utbetalingVM),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                createPeriodeLabel(utbetalingVM),
                new Label("belop", utbetalingVM.getBelop()),
                new Label("valuta", utbetalingVM.getValuta()),
                new WebMarkupContainer("mottakerIndikator")
                        .add(new AttributeAppender("class", utbetalingVM.getMottakerkode()).setSeparator(" ")),
                new Label("statusTekst", utbetalingVM.getStatus())
        );
    }

    private Label createUtbetalingsDatoLabel(UtbetalingVM utbetalingVM) {
        return new Label("utbetalingsDato", getDatoModel(utbetalingVM.getUtbetalingsDato(), "utbetalingdato.mangler"));
    }

    private Label createPeriodeLabel(UtbetalingVM utbetalingVM) {
        IModel<String> periodeModel = getPeriodeModel(
                getDatoModel(utbetalingVM.getStartDato(), "startdato.mangler"),
                getDatoModel(utbetalingVM.getSluttDato(), "sluttdato.mangler"));
        return new Label("periode", periodeModel);
    }

    private IModel<String> getDatoModel(DateTime dato, String resourceKey) {
        return dato != null ?
                Model.of(kortUtenLiteral(dato)) :
                new StringResourceModel(resourceKey, this, null);
    }

    private IModel<String> getPeriodeModel(final IModel startDatoModel, final IModel sluttDatoModel) {
        return new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return startDatoModel.getObject()  + " - " + sluttDatoModel.getObject();
            }
        };
    }

}
