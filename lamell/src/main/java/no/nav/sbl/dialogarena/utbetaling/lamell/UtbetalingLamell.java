package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterForm;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterProperties;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.OppsummeringProperties;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsHolder;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;
import static org.apache.wicket.model.Model.ofList;

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");

    @Inject
    private UtbetalingService utbetalingService;

    private UtbetalingsHolder utbetalingsDatakilde;
    private FilterProperties filter;
    private OppsummeringPanel oppsummeringPanel;
    private MarkupContainer utbetalingerContainer;
    private ListView<Utbetaling> utbetalingListView;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

        instansierFelter(fnr);

        add(
                new FeedbackPanel("feedbackpanel").setOutputMarkupId(true),
                new FilterForm("filterForm", filter),
                oppsummeringPanel,
                utbetalingerContainer
        );
    }

    private void instansierFelter(String fnr) {
        utbetalingsDatakilde = new UtbetalingsHolder(fnr, utbetalingService);
        filter = new FilterProperties(DEFAULT_STARTDATO, DEFAULT_SLUTTDATO, true, true);
        oppsummeringPanel = createOppsummeringPanel(utbetalingsDatakilde.getUtbetalinger());
        utbetalingListView = createUtbetalingListView();
        utbetalingerContainer = (WebMarkupContainer) new WebMarkupContainer("utbetalingerContainer")
                .add(utbetalingListView)
                .setOutputMarkupId(true);
    }

    private OppsummeringPanel createOppsummeringPanel(List<Utbetaling> liste) {
        return (OppsummeringPanel) new OppsummeringPanel("oppsummeringPanel", createOppsummeringPropertiesModel(liste))
                .setOutputMarkupId(true);
    }

    private CompoundPropertyModel<OppsummeringProperties> createOppsummeringPropertiesModel(List<Utbetaling> liste) {
        return new CompoundPropertyModel<>(new OppsummeringProperties(liste, filter.getStartDato(), filter.getSluttDato()));
    }

    private ListView<Utbetaling> createUtbetalingListView() {
        return new ListView<Utbetaling>("utbetalinger", ofList(utbetalingsDatakilde.getSynligeUtbetalinger(filter.getParams()))) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", item.getModelObject()));
            }
        };
    }

    @RunOnEvents(FilterProperties.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        List<Utbetaling> synligeUtbetalinger = utbetalingsDatakilde.getSynligeUtbetalinger(filter.getParams());
        oppsummeringPanel.setDefaultModelObject(new OppsummeringProperties(
                synligeUtbetalinger,
                filter.getStartDato(),
                filter.getSluttDato()));
        utbetalingListView.setModelObject(synligeUtbetalinger);

        target.add(utbetalingerContainer, oppsummeringPanel);
    }

}
