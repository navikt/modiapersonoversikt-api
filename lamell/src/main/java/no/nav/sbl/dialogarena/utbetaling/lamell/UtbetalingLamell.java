package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.Filter;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterForm;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;
import static org.apache.wicket.model.Model.ofList;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");

    @Inject
    private UtbetalingService utbetalingService;
    private final Filter filter;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

//        Mottaker mottaker = new Mottaker("test", "test", "test");

        filter = new Filter(DEFAULT_STARTDATO, DEFAULT_SLUTTDATO, true, true);
        ListView utbetalingListView = createUtbetalingListView(fnr);

        FeedbackPanel feedbackpanel = new FeedbackPanel("feedbackpanel");
        feedbackpanel.setOutputMarkupId(true);
        setOutputMarkupId(true);
        add(
                feedbackpanel,
                new FilterForm("filterForm", filter, utbetalingListView, feedbackpanel),
                utbetalingListView
        );
    }

    private ListView<Utbetaling> createUtbetalingListView(final String fnr) {
        return new ListView<Utbetaling>("utbetalinger", ofList(utbetalingService.hentUtbetalinger(fnr, filter.getStartDate(), filter.getSluttDate()))) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", item.getModelObject()));
            }
        };
    }
}
