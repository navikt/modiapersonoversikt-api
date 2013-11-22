package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.Filter;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterForm;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.LocalDate;

import javax.inject.Inject;

import static org.apache.wicket.model.Model.ofList;
import static org.joda.time.DateTime.now;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");

    private static final LocalDate FILTER_STARTDATO = now().minusMonths(3).toLocalDate();
    private static final LocalDate FILTER_SLUTTDATO = now().toLocalDate();

    @Inject
    private UtbetalingService utbetalingService;

    private Filter filter;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

        Mottaker mottaker = new Mottaker("test", "test", "test");

        this.filter = new Filter(FILTER_STARTDATO, FILTER_SLUTTDATO, mottaker);
        ListView utbetalingListView = createUtbetalingListView(fnr);

        add(
                new FilterForm("filterForm", filter, utbetalingListView),
                utbetalingListView
        );
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
