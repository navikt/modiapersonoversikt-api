package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.LocalDate;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;

public class FilterForm extends Form<Void> {

    private static final LocalDate MIN_DATO =  LocalDate.now().minusYears(150);
    private static final LocalDate MAKS_DATO = LocalDate.now();

    public FilterForm(String id, Filter filter, final FeedbackPanel feedbackpanel) {
        super(id);

        add(createMottakerButton("visBruker", filter, feedbackpanel));
        add(createMottakerButton("visArbeidsgiver", filter, feedbackpanel));

        add(createDateRangePicker(filter));
        add(createAjaxFormSubmitBehaviour());
    }

    private AjaxCheckBox createMottakerButton(final String mottaker, Filter filter, final FeedbackPanel feedbackpanel) {
        return new AjaxCheckBox(mottaker, new PropertyModel<Boolean>(filter, mottaker)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        };
    }

    private DateRangePicker createDateRangePicker(Filter filter) {
        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator().withMaxDate("0d").build();
        DateRangeModel dateRangeModel = new DateRangeModel(filter.getStartDato(), filter.getSluttDato());

        return new DateRangePicker("datoFilter", dateRangeModel, datePickerConfigurator, MIN_DATO, MAKS_DATO);
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
