package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.Filter;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterForm;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsDatakilde.getKilde;
import static org.apache.wicket.model.Model.ofList;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");

    @Inject
    private UtbetalingService utbetalingService;

    private MarkupContainer utbetalingerContainer;
    private final Filter filter;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

        getKilde().refreshUtbetalinger(fnr, DEFAULT_STARTDATO.toDateTimeAtStartOfDay(), DEFAULT_SLUTTDATO.toDateTimeAtStartOfDay(), utbetalingService);

        filter = new Filter(DEFAULT_STARTDATO, DEFAULT_SLUTTDATO, true, true);

        utbetalingerContainer = new WebMarkupContainer("utbetalingerContainer").add(createUtbetalingListView(fnr));
        utbetalingerContainer.setOutputMarkupId(true);

        FeedbackPanel feedbackpanel = new FeedbackPanel("feedbackpanel");
        feedbackpanel.setOutputMarkupId(true);

        add(
                new OppsummeringPanel("oppsummeringPanel", filter.getPeriode()),
                feedbackpanel,
                new FilterForm("filterForm", filter),
                utbetalingerContainer
        );
    }

    private ListView<Utbetaling> createUtbetalingListView(final String fnr) {
        DateTime startDato = filter.getStartDato().toDateTimeAtStartOfDay();
        DateTime sluttDato = filter.getSluttDato().toDateTimeAtStartOfDay();

        return new ListView<Utbetaling>("utbetalinger", ofList(utbetalingService.hentUtbetalinger(fnr, startDato, sluttDato))) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                Utbetaling utbetaling = item.getModelObject();

                item.add(new UtbetalingPanel("utbetaling", utbetaling));

                item.add(visibleIf(
                        new Model<>(filter.filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate()) &&
                                filter.filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType()))));
            }
        };
    }

    @RunOnEvents(Filter.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        target.add(utbetalingerContainer);
    }

}
