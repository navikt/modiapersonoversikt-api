package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import com.googlecode.wicket.jquery.ui.form.slider.AjaxRangeSlider;
import com.googlecode.wicket.jquery.ui.form.slider.RangeValue;
import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.joda.time.LocalDate;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static org.joda.time.LocalDate.now;
import static org.joda.time.Months.monthsBetween;

public class FilterForm extends Form {

    public static final JavaScriptResourceReference JQUERY_UI_SLIDER = new JavaScriptResourceReference(Filter.class, "jquery-ui-1.10.3.custom.min.js");

    private static final int AAR_TILBAKE = 3;
    private static final int INITIELL_DATOINTERVALL_MIN = 33;
    private static final int INITIELL_DATOINTERVALL_MAKS = 36;

    private static final LocalDate MIN_DATO = now().minusYears(AAR_TILBAKE);
    private static final LocalDate MAKS_DATO =  now();

    private Filter filter;
    private IModel<RangeValue> rangeSliderValue;

    public FilterForm(String id, Filter filter) {
        super(id);
        setOutputMarkupId(true);

        this.filter = filter;
        this.rangeSliderValue = new Model<>(new RangeValue(INITIELL_DATOINTERVALL_MIN, INITIELL_DATOINTERVALL_MAKS));

        add(createMottakerButton("visBruker"));
        add(createMottakerButton("visArbeidsgiver"));

        add(createDateRangePicker());

        add(createDateRangeSlider());

        add(createAjaxFormSubmitBehaviour());
    }

    private AjaxCheckBox createMottakerButton(final String mottaker) {
        return new AjaxCheckBox(mottaker, new PropertyModel<Boolean>(filter, mottaker)) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        };
    }

    private DateRangePicker createDateRangePicker() {
        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate("-" + AAR_TILBAKE + "y")
                .withMaxDate("0d")
                .withYearRange("-" + AAR_TILBAKE + "y:c")
                .build();
        DateRangeModel dateRangeModel = new DateRangeModel(filter.getStartDato(), filter.getSluttDato());

        return new DateRangePicker("datoFilter", dateRangeModel, datePickerConfigurator, MIN_DATO, MAKS_DATO);
    }

    private AjaxRangeSlider createDateRangeSlider() {
        return new AjaxRangeSlider("datoSlider", rangeSliderValue) {
            @Override
            public void onValueChanged(AjaxRequestTarget target, Form<?> form) {
                oppdaterFilterDatoModell(this.getModelObject().getLower(), this.getModelObject().getUpper());
                sendFilterEndretEvent();

                target.add(FilterForm.this);
            }
        }.setMin(0).setMax(INITIELL_DATOINTERVALL_MAKS);
    }

    private AjaxFormSubmitBehavior createAjaxFormSubmitBehaviour() {
        return new AjaxFormSubmitBehavior("onchange") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                oppdaterDatointervallModell();
                sendFilterEndretEvent();

                target.add(FilterForm.this);
            }
        };
    }

    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, Filter.ENDRET);
    }

    private void oppdaterFilterDatoModell(int startMaaned, int sluttMaaned) {
        filter.getStartDato().setObject(rangeValueIntTilLocalDate(startMaaned));
        filter.getSluttDato().setObject(rangeValueIntTilLocalDate(sluttMaaned));

        sendFilterEndretEvent();
    }

    private void oppdaterDatointervallModell() {
        rangeSliderValue.setObject(filterDatoTilRangeValue(filter.getStartDato(), filter.getSluttDato()));
    }

    private static LocalDate rangeValueIntTilLocalDate(Integer rangeValueInt) {
        return now().minusYears(AAR_TILBAKE).plusMonths(rangeValueInt);
    }

    private static RangeValue filterDatoTilRangeValue(IModel<LocalDate> startDatoFilterModell, IModel<LocalDate> sluttDatoFilterModell) {
        LocalDate datoBase = now().minusYears(AAR_TILBAKE);

        return new RangeValue(monthsBetween(datoBase, startDatoFilterModell.getObject()).getMonths(),
                monthsBetween(datoBase, sluttDatoFilterModell.getObject()).getMonths());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(JQUERY_UI_SLIDER));
    }
}
