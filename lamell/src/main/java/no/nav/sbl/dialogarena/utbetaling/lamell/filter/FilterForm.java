package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.LocalDate;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;

public class FilterForm extends Form<Void> {

    private static final int AAR_TILBAKE = 100;

    public FilterForm(String id, Filter filter) {
        super(id);

        add(createMottakerButton("visBruker", filter));
        add(createMottakerButton("visArbeidsgiver", filter));

        add(createDateRangePicker(filter));
        add(createAjaxFormSubmitBehaviour());
    }

    private AjaxCheckBox createMottakerButton(final String mottaker, Filter filter) {
        return new AjaxCheckBox(mottaker, new PropertyModel<Boolean>(filter, mottaker)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        };
    }

    private DateRangePicker createDateRangePicker(Filter filter) {
        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate("-" + AAR_TILBAKE + "y")
                .withMaxDate("0d")
                .withYearRange("-" + AAR_TILBAKE + "y:c")
                .build();
        DateRangeModel dateRangeModel = new DateRangeModel(filter.getStartDato(), filter.getSluttDato());
        LocalDate minDato = LocalDate.now().minusYears(AAR_TILBAKE);
        LocalDate maksDato = LocalDate.now();

        return new DateRangePicker("datoFilter", dateRangeModel, datePickerConfigurator, minDato, maksDato);
    }

    private AjaxFormSubmitBehavior createAjaxFormSubmitBehaviour() {
        return new AjaxFormSubmitBehavior("onchange") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                send(getPage(), Broadcast.DEPTH, Filter.ENDRET);
            }
        };
    }

}
