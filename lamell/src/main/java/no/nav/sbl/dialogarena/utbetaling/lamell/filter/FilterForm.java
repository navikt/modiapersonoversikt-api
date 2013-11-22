package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListView;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;

public class FilterForm extends Form {

    private Filter filter;

    public FilterForm(String id, Filter filter, ListView utbetalingListView) {
        super(id);

        this.filter = filter;

        add(createDateRangePicker());
        add(createAjaxFormSubmitBehaviour(utbetalingListView));
    }

    private AjaxFormSubmitBehavior createAjaxFormSubmitBehaviour(final ListView listView) {
        return new AjaxFormSubmitBehavior("onsubmit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(listView);
            }
        };
    }

    private DateRangePicker createDateRangePicker() {
        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMaxDate("0d")
                .build();
        return new DateRangePicker("datoFilter",
                new DateRangeModel(filter.getStartDato(), filter.getSluttDato()), datePickerConfigurator, filter.getStartDato().getObject(), filter.getSluttDato().getObject());
    }

}
