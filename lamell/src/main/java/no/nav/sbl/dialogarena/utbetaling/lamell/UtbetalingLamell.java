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
import java.util.ArrayList;
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

        ListView<List<Utbetaling>> maanedsPanelet = createMaanedsPanelet();

        utbetalingerContainer = (WebMarkupContainer) new WebMarkupContainer("utbetalingerContainer")
                .add(maanedsPanelet)
                .setOutputMarkupId(true);
    }

    private OppsummeringPanel createOppsummeringPanel(List<Utbetaling> liste) {
        return (OppsummeringPanel) new OppsummeringPanel("oppsummeringPanel", createOppsummeringPropertiesModel(liste))
                .setOutputMarkupId(true);
    }

    private CompoundPropertyModel<OppsummeringProperties> createOppsummeringPropertiesModel(List<Utbetaling> liste) {
        return new CompoundPropertyModel<>(new OppsummeringProperties(liste, filter.getStartDato(), filter.getSluttDato()));
    }


    private ListView<List<Utbetaling>> createMaanedsPanelet() {
        List<List<Utbetaling>> maanedsListe = utbetalingsDatakilde.hentFiltrertUtbetalingerPerMaaned(filter.getParams());
        System.out.println("maanedsListe.size() = " + maanedsListe.size());

        ListView<List<Utbetaling>> listView = new ListView<List<Utbetaling>>("maanedsPaneler", maanedsListe) {
            @Override
            protected void populateItem(ListItem<List<Utbetaling>> item) {
                CompoundPropertyModel<OppsummeringProperties> model = createOppsummeringPropertiesModel(item.getModelObject());
                MaanedsPanel maanedsPanel = new MaanedsPanel("maanedsPanel", model);
                item.add(maanedsPanel);
            }
        };

        return listView;
    }


    @RunOnEvents(FilterProperties.ENDRET)
    @SuppressWarnings("unused")
    private void oppdaterUtbetalingsListe(AjaxRequestTarget target) {
        List<Utbetaling> synligeUtbetalinger = utbetalingsDatakilde.getSynligeUtbetalinger(filter.getParams());
        oppsummeringPanel.setDefaultModelObject(new OppsummeringProperties(
                synligeUtbetalinger,
                filter.getStartDato(),
                filter.getSluttDato()));
        //utbetalingListView.setModelObject(synligeUtbetalinger);

        target.add(utbetalingerContainer, oppsummeringPanel);
    }

}
