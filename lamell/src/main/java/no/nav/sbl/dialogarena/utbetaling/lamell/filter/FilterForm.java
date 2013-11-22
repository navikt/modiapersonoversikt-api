package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;

public class FilterForm extends Form {
    private static final Logger LOG = LoggerFactory.getLogger(FilterForm.class);
    private Filter filter;

    public FilterForm(String id, Filter filter, ListView utbetalingListView, final FeedbackPanel feedbackpanel) {
        super(id);

        this.filter = filter;

        add(createMottakerButton("brukerCheckbox", feedbackpanel, "brukerLabel", "Bruker"));
        add(createMottakerButton("arbeidsgiverCheckbox", feedbackpanel, "arbeidsgiverLabel", "Arbeidsgiver"));

        add(createDateRangePicker());
        add(createAjaxFormSubmitBehaviour(utbetalingListView, feedbackpanel));
    }

    private AjaxCheckBox createMottakerButton(final String mottaker, final FeedbackPanel feedbackpanel, String labelId, String labelTekst) {
        AjaxCheckBox checkBox = new AjaxCheckBox(mottaker, new PropertyModel<Boolean>(filter, mottaker)) {

            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                ajaxRequestTarget.add(feedbackpanel);
                final Boolean value = (Boolean) getDefaultModelObject();
                info("Trykket på " + mottaker + ". Verdi: " + value);
                LOG.info("Trykket på " + mottaker + " Verdi: " + value);
            }
        };
        return checkBox;
    }

    private AjaxFormSubmitBehavior createAjaxFormSubmitBehaviour(final ListView listView, final FeedbackPanel feedbackpanel) {
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
