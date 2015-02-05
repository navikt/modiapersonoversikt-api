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

public class UtbetalingWidgetPanel extends GenericPanel<HovedytelseVM> {

    public UtbetalingWidgetPanel(String id, IModel<HovedytelseVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        HovedytelseVM hovedytelseVM = model.getObject();

        add(
                createUtbetalingsDatoLabel(hovedytelseVM),
                new Label("beskrivelse", hovedytelseVM.getBeskrivelse()),
                createPeriodeLabel(hovedytelseVM),
                new Label("belop", hovedytelseVM.getBelop()),
                new WebMarkupContainer("mottakerIndikator")
                        .add(new AttributeAppender("class", hovedytelseVM.getMottakertype()).setSeparator(" ")),
                new Label("statusTekst", hovedytelseVM.getStatus())
        );
    }

    private Label createUtbetalingsDatoLabel(HovedytelseVM hovedytelseVM) {
        return new Label("utbetalingsDato", getDatoModel(hovedytelseVM.getUtbetalingsDato(), "utbetalingdato.mangler"));
    }

    private Label createPeriodeLabel(HovedytelseVM hovedytelseVM) {
        IModel<String> periodeModel = getPeriodeModel(
                getDatoModel(hovedytelseVM.getStartDato(), "startdato.mangler"),
                getDatoModel(hovedytelseVM.getSluttDato(), "sluttdato.mangler"));
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
