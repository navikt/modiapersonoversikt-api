package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.LocalDate;

import javax.inject.Inject;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static org.apache.wicket.model.Model.ofList;
import static org.joda.time.DateTime.now;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");
    public static final String MIN_DATE = "-100y";
    public static final String MAX_DATE = "0d";

    //MOD-4962 spesifiserer disse
    public static final LocalDate START_DATO = now().minusMonths(3).toLocalDate();
    public static final LocalDate SLUTT_DATO = now().toLocalDate();

    @Inject
    private UtbetalingService utbetalingService;

    protected IModel<LocalDate> startDato;
    protected IModel<LocalDate> sluttDato;

    public UtbetalingLamell(String id, String fnr) {
        super(id);
        add(
                createFilterForm(),
                createUtbetalingListView(fnr)
        );
    }

    private MarkupContainer createFilterForm() {
        startDato = new Model<>(START_DATO);
        sluttDato = new Model<>(SLUTT_DATO);

        return new Form("filterForm").add(createDateRangePicker());
    }

    private DateRangePicker createDateRangePicker() {
        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate(MIN_DATE)
                .withMaxDate(MAX_DATE)
                .build();
        return new DateRangePicker("datoFilter", new DateRangeModel(startDato, sluttDato), datePickerConfigurator, startDato.getObject(), sluttDato.getObject());
    }

    private ListView<Utbetaling> createUtbetalingListView(final String fnr) {
        return new ListView<Utbetaling>("utbetalinger", ofList(utbetalingService.hentUtbetalinger(fnr))) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", item.getModelObject()));
            }
        };
    }
}
