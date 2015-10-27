package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.modig.wicket.component.daterangepicker.StrictDateRangePicker;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.*;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.PeriodeVelger.EGENDEFINERT;
import static org.joda.time.LocalDate.now;

public class PeriodeForm extends Panel {

    private static final int TRE_AAR_TILBAKE = 3;

    private FilterParametere filterParametere;
    private MarkupContainer datovelgerContainer;

    public PeriodeForm(String id, FilterParametere filterParametere) {
        super(id);
        this.filterParametere = filterParametere;
        datovelgerContainer = createDatovelgerWrapper("datovelger");
        add(createForm("periodeForm"));
    }

    private Form createForm(String id) {
        Form form = new Form(id);
        form.add(createPeriodeVelger("periodeVelger"),
                datovelgerContainer,
                FeedbackLabel.create(datovelgerContainer.get("datoFilter:startDate")),
                FeedbackLabel.create(datovelgerContainer.get("datoFilter:endDate")),
                createSokKnapp());
        return form;
    }

    private WebMarkupContainer createDatovelgerWrapper(String id) {
        WebMarkupContainer container = new WebMarkupContainer(id);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisibilityAllowed(false);
        container.add(createDateRangePicker("datoFilter"));
        return container;
    }

    private Component createPeriodeVelger(String id) {
        PropertyModel<FilterParametere.PeriodeVelger> periodeVelgerModel = new PropertyModel<>(Model.of(filterParametere), "periodeVelgerValg");
        RadioChoice<FilterParametere.PeriodeVelger> velger = new RadioChoice<>(id, periodeVelgerModel, asList(FilterParametere.PeriodeVelger.values()));

        velger.setSuffix("");
        velger.setChoiceRenderer(new ChoiceRenderer<FilterParametere.PeriodeVelger>() {
            @Override
            public Object getDisplayValue(FilterParametere.PeriodeVelger enumVal) {
                String displayKey = enumVal.name().toLowerCase().replace("_", "");
                return getString("utbetaling.lamell.filter.valgtperiode." + displayKey);
            }
        });

        velger.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                FilterParametere.PeriodeVelger valgtPeriode = filterParametere.periodeVelgerValg;
                if (valgtPeriode.equals(EGENDEFINERT)) {
                    datovelgerContainer.setVisibilityAllowed(true);
                    target.add(datovelgerContainer);
                } else {
                    datovelgerContainer.setVisibilityAllowed(false);
                    target.add(datovelgerContainer);
                }
            }
        });

        return velger;
    }

    private DateRangePicker createDateRangePicker(String id) {
        LocalDate minDato = now().minusYears(TRE_AAR_TILBAKE).withDayOfYear(1);
        LocalDate maksDato = now();

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");

        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate(formatter.print(minDato))
                .withMaxDate("0d")
                .withYearRange("-" + TRE_AAR_TILBAKE + "y:c")
                .withParameter("showOn", "button")
                .build();

        DateRangeModel dateRangeModel = new DateRangeModel(
                new PropertyModel<LocalDate>(filterParametere, "startDato"),
                new PropertyModel<LocalDate>(filterParametere, "sluttDato"));

        return new StrictDateRangePicker(id, dateRangeModel, datePickerConfigurator, minDato, maksDato);
    }

    private AjaxButton createSokKnapp() {
        AjaxButton button = new AjaxButton("sok", new StringResourceModel("utbetaling.lamell.filter.periode.sok", this, null)) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterParametere.toggleAlleYtelser(true);
                sendFilterEndretEvent();
                target.add(datovelgerContainer);
                target.appendJavaScript("Utbetalinger.skjulSnurrepipp();");
                FeedbackLabel.addFormLabelsToTarget(target, form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                sendFilterFeilEvent();
                FeedbackLabel.addFormLabelsToTarget(target, form);
            }
        };

        return button;
    }

    @SuppressWarnings("unused")
    @RunOnEvents(PERIODEVALG)
    private void oppdaterPeriodevelger(AjaxRequestTarget target) {
        target.add(datovelgerContainer);
    }


    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, FILTER_ENDRET);
    }

    private void sendFilterFeilEvent() {
        send(getPage(), Broadcast.DEPTH, FILTER_FEILET);
    }
}
