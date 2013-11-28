package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterProperties;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterForm;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.OppsummeringProperties;
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

import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsDatakilde.getKilde;
import static org.apache.wicket.model.Model.ofList;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");

    private OppsummeringPanel oppsummeringPanel;
    private OppsummeringProperties oppsummeringProperties;

    @Inject
    private UtbetalingService utbetalingService;
    private MarkupContainer utbetalingerContainer;
    private final FilterProperties filter;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

        getKilde().refreshUtbetalinger(fnr, DEFAULT_STARTDATO.toDateTimeAtStartOfDay(), DEFAULT_SLUTTDATO.toDateTimeAtStartOfDay(), utbetalingService);

        filter = new FilterProperties(DEFAULT_STARTDATO, DEFAULT_SLUTTDATO, true, true);

        createOppsummering(getKilde().getUtbetalinger());
        ListView<Utbetaling> listView = createUtbetalingListView();

        utbetalingerContainer = new WebMarkupContainer("utbetalingerContainer");
        utbetalingerContainer.add(listView, oppsummeringPanel);
        utbetalingerContainer.setOutputMarkupId(true);

        FeedbackPanel feedbackpanel = new FeedbackPanel("feedbackpanel");
        feedbackpanel.setOutputMarkupId(true);
        add(
                feedbackpanel,
                new FilterForm("filterForm", filter),
                utbetalingerContainer
        );
    }

    private void createOppsummering(List<Utbetaling> liste) {
        oppsummeringProperties = new OppsummeringProperties(liste, filter.getStartDato(), filter.getSluttDato());
        oppsummeringPanel = new OppsummeringPanel("oppsummeringPanel", getOppsummeringModel());
        oppsummeringPanel.setOutputMarkupId(true);
        oppsummeringPanel.setOutputMarkupPlaceholderTag(true);
    }


    private Model<OppsummeringProperties> getOppsummeringModel() {
        return new Model<OppsummeringProperties>(oppsummeringProperties) {};
    }

    private ListView<Utbetaling> createUtbetalingListView() {
        DateTime startDato = filter.getStartDato().toDateTimeAtStartOfDay();
        DateTime sluttDato = filter.getSluttDato().toDateTimeAtStartOfDay();

        return new ListView<Utbetaling>("utbetalinger", ofList(getKilde().hentUtbetalinger(startDato, sluttDato))) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                Utbetaling utbetaling = item.getModelObject();
                System.out.println("item = " + item);

                item.add(new UtbetalingPanel("utbetaling", utbetaling));
                item.add(visibleIf(
                        new Model<>(filter.filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate()) &&
                                filter.filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType()))));
            }
        };
    }

    @RunOnEvents(FilterProperties.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        target.add(utbetalingerContainer);

        List<Utbetaling> synligeUtbetalinger = getKilde().getSynligeUtbetalinger(filter.getParams());
        oppsummeringProperties = new OppsummeringProperties(synligeUtbetalinger, filter.getStartDato(), filter.getSluttDato());
//        oppsummeringPanel.setOppsummering(oppsummeringProperties);
//
//        target.add(oppsummeringPanel);
    }

}
