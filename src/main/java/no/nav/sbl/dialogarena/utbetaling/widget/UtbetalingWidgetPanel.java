package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.time.Datoformat.kortUtenLiteral;
import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.erGyldigStartSluttVerdier;

public class UtbetalingWidgetPanel extends GenericPanel<HovedutbetalingVM> {

    public UtbetalingWidgetPanel(String id, IModel<HovedutbetalingVM> model) {
        super(id, model);
        setOutputMarkupId(true);
        HovedutbetalingVM hovedutbetalingVM = model.getObject();
        setMarkupId("hovedutbetalingwidget-" + hovedutbetalingVM.getId());

        add(
                createUtbetalingsDatoLabel(hovedutbetalingVM),
                new Label("ytelse", hovedutbetalingVM.getBeskrivelse()),
                createPeriodeLabel(hovedutbetalingVM),
                new Label("belop", hovedutbetalingVM.getBelop()),
                createStatusLabel(hovedutbetalingVM),
                new Label("utbetaltTil", hovedutbetalingVM.getMottaker())
        );
    }

    private Label createStatusLabel(HovedutbetalingVM hovedutbetalingVM) {
        Label label = new Label("statusTekst", hovedutbetalingVM.getStatus());
        if(hovedutbetalingVM.isUtbetalt()) {
            label.add(new AttributeAppender("class", "utbetalt").setSeparator(" "));
        }
        return label;
    }

    private Label createUtbetalingsDatoLabel(HovedutbetalingVM hovedutbetalingVM) {
        return new Label("utbetalingsDato", getDatoModel(hovedutbetalingVM.getHovedytelseDato(), Datoformat.Date, "utbetalingdato.mangler"));
    }

    private Label createPeriodeLabel(HovedutbetalingVM hovedutbetalingVM) {
        if (erGyldigStartSluttVerdier(hovedutbetalingVM.getStartDato(), hovedutbetalingVM.getSluttDato())) {
            return new Label("periode", getPeriodeModel(
                    getDatoModel(hovedutbetalingVM.getStartDato(), Datoformat.KortDatoUtenLiteral, "startdato.mangler"),
                    getDatoModel(hovedutbetalingVM.getSluttDato(), Datoformat.KortDatoUtenLiteral, "sluttdato.mangler")
            ));
        }
        return (Label) new Label("periode",
                new StringResourceModel("utbetaling.lamell.utbetaling.udefinertperiode", UtbetalingWidgetPanel.this, null).getString())
                .add(new AttributeAppender("class", "kursiv").setSeparator(" "));
    }

    private IModel<String> getDatoModel(DateTime dato, Datoformat datoformat, String resourceKey) {
        return dato != null ?
                Model.of(formatDate(dato, datoformat)) :
                new StringResourceModel(resourceKey, this, null);
    }

    private String formatDate(DateTime dato, Datoformat datoformat) {
        if(datoformat == Datoformat.KortDatoUtenLiteral) {
            return kortUtenLiteral(dato);
        }
        return WidgetDateFormatter.date(dato);
    }

    private IModel<String> getPeriodeModel(final IModel startDatoModel, final IModel sluttDatoModel) {
        return new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return startDatoModel.getObject()  + " - " + sluttDatoModel.getObject();
            }
        };
    }

    private enum Datoformat{
        KortDatoUtenLiteral, Date
    }

}
