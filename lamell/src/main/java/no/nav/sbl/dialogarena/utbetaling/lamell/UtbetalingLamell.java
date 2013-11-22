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
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.LocalDate;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static org.apache.wicket.model.Model.ofList;
import static org.joda.time.DateTime.now;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");
    private static final LocalDate FILTER_STARTDATO = now().minusMonths(3).toLocalDate();
    private static final LocalDate FILTER_SLUTTDATO = now().toLocalDate();
    @Inject
    private UtbetalingService utbetalingService;

    private MarkupContainer utbetalingerContainer;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

        Filter filter = new Filter(FILTER_STARTDATO, FILTER_SLUTTDATO, true, true);

        utbetalingerContainer = new WebMarkupContainer("utbetalingerContainer").add(createUtbetalingListView(fnr, filter));
        utbetalingerContainer.setOutputMarkupId(true);

        FeedbackPanel feedbackpanel = new FeedbackPanel("feedbackpanel");
        feedbackpanel.setOutputMarkupId(true);
        setOutputMarkupId(true);
        add(
                feedbackpanel,
                new FilterForm("filterForm", filter, feedbackpanel),
                utbetalingerContainer
        );
    }

    private ListView<Utbetaling> createUtbetalingListView(final String fnr, final Filter filter) {
        return new ListView<Utbetaling>("utbetalinger", ofList(utbetalingService.hentUtbetalinger(fnr))) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", item.getModelObject()));
                item.add(visibleIf(new Model<>(filtrerDatoer(item) && filtrerMottaker(item))));
            }

            private boolean filtrerDatoer(ListItem<Utbetaling> item) {
                LocalDate utbetalingsDato = item.getModelObject().getUtbetalingsDato().toLocalDate();
                LocalDate filterStartDato = filter.getStartDato().getObject();
                LocalDate filterSluttDato = filter.getSluttDato().getObject();

                return utbetalingsDato.isAfter(filterStartDato)
                        && utbetalingsDato.isBefore(filterSluttDato);
            }

            private boolean filtrerMottaker(ListItem<Utbetaling> item) {
                String mottakerKode = item.getModelObject().getMottaker().getMottakertypeKode();
                boolean visArbeidsgiver = filter.getVisArbeidsgiver() && "arbeidsgiver".equalsIgnoreCase(mottakerKode);
                boolean visBruker = filter.getVisBruker() && "bruker".equalsIgnoreCase(mottakerKode);
                return visArbeidsgiver || visBruker;
            }
        };
    }

    @RunOnEvents(Filter.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        target.add(utbetalingerContainer);
    }

}
