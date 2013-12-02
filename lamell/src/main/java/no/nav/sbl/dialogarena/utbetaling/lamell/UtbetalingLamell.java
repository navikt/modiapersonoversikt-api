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

public class UtbetalingLamell extends Lerret {

    public static final PackageResourceReference UTBETALING_LAMELL_LESS = new PackageResourceReference(UtbetalingLamell.class, "utbetaling.less");
    @Inject
    private UtbetalingService utbetalingService;
    private UtbetalingsHolder utbetalingsDatakilde;
    private FilterProperties filter;
    private OppsummeringPanel oppsummeringPanel;
    private MarkupContainer utbetalingerContainer;
    private ListView<List<Utbetaling>> maanedsListView;

    public UtbetalingLamell(String id, String fnr) {
        super(id);

        instansierFelter(fnr);

        add(
                new FeedbackPanel("feedbackpanel").setOutputMarkupId(true),
                new FilterForm("filterForm", filter),
                oppsummeringPanel.setOutputMarkupId(true),
                utbetalingerContainer.setOutputMarkupId(true)
        );
    }

    private void instansierFelter(String fnr) {
        utbetalingsDatakilde = new UtbetalingsHolder(fnr, utbetalingService);
        filter = new FilterProperties(DEFAULT_STARTDATO, DEFAULT_SLUTTDATO, true, true);
        oppsummeringPanel = createOppsummeringPanel(utbetalingsDatakilde.getSynligeUtbetalinger(filter.getParams()));
        utbetalingerContainer = new WebMarkupContainer("utbetalingerContainer").add(createMaanedsPanelet());
    }

    private OppsummeringPanel createOppsummeringPanel(List<Utbetaling> liste) {
        return new OppsummeringPanel("totalOppsummeringPanel", createOppsummeringPropertiesModel(liste));
    }

    private CompoundPropertyModel<OppsummeringProperties> createOppsummeringPropertiesModel(List<Utbetaling> liste) {
        return new CompoundPropertyModel<>(new OppsummeringProperties(liste, filter.getStartDato(), filter.getSluttDato()));
    }

    private ListView<List<Utbetaling>> createMaanedsPanelet() {
        List<List<Utbetaling>> maanedsListe = utbetalingsDatakilde.hentFiltrertUtbetalingerPerMaaned(filter.getParams());

        maanedsListView = new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                CompoundPropertyModel<OppsummeringProperties> model = createOppsummeringPropertiesModel(item.getModelObject());
                item.add(new MaanedsPanel("maanedsPanel", model));
            }
        };
        maanedsListView.setOutputMarkupId(true);

        return maanedsListView;
    }

    @RunOnEvents(FilterProperties.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        List<Utbetaling> synligeUtbetalinger = utbetalingsDatakilde.getSynligeUtbetalinger(filter.getParams());
        oppsummeringPanel.setDefaultModelObject(new OppsummeringProperties(
                synligeUtbetalinger,
                filter.getStartDato(),
                filter.getSluttDato()));

        List<List<Utbetaling>> maanedsListe = utbetalingsDatakilde.hentFiltrertUtbetalingerPerMaaned(filter.getParams());
        maanedsListView.setModelObject(maanedsListe);

        target.add(utbetalingerContainer, oppsummeringPanel);
    }

}
