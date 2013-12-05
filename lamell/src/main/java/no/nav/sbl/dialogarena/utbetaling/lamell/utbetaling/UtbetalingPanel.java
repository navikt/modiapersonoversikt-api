package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.List;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, Utbetaling utbetaling) {
        super(id, new CompoundPropertyModel<>(utbetaling));
        WebMarkupContainer bilagWrapper = createBilagWrapper(utbetaling.getBilag());

        add(
                bilagWrapper,
                createExpandButton(bilagWrapper),
                new Label("statuskode"),
                new Label("kortUtbetalingsDato"),
                new Label("beskrivelse"),
                new Label("belopMedValuta"),
                new Label("kontoNr"),
                new Label("periodeMedKortDato"),
                new Label("mottaker.navn")
        );
    }

    private WebMarkupContainer createBilagWrapper(List<Bilag> bilagsliste) {
        return (WebMarkupContainer) new WebMarkupContainer("bilagWrapper")
                .add(createBilagListView(bilagsliste))
                .setOutputMarkupPlaceholderTag(true)
                .setVisibilityAllowed(false);
    }

    private ListView<Bilag> createBilagListView(List<Bilag> bilagsliste) {
        return new ListView<Bilag>("bilag-liste", bilagsliste) {
            @Override
            protected void populateItem(ListItem<Bilag> item) {
                Bilag bilag = item.getModelObject();
                item.add(new Label("melding", bilag.getMelding()));
                item.add(createPosteringsDetaljListView(bilag.getPosteringsDetaljer()));
            }
        };
    }

    private ListView<PosteringsDetalj> createPosteringsDetaljListView(List<PosteringsDetalj> posteringsDetaljListe) {
        return new ListView<PosteringsDetalj>("posteringsdetalj-liste", posteringsDetaljListe) {
            @Override
            protected void populateItem(ListItem<PosteringsDetalj> item) {
                PosteringsDetalj posteringsDetalj = item.getModelObject();
                item.add(new Label("hovedbeskrivelse", posteringsDetalj.getHovedBeskrivelse()));
                item.add(new Label("underbeskrivelse", posteringsDetalj.getUnderBeskrivelse()));
                item.add(new Label("kontonr", posteringsDetalj.getKontoNr()));
                item.add(new Label("sats", posteringsDetalj.getSats()));
                item.add(new Label("antall", posteringsDetalj.getAntall()));
            }
        };
    }

    private AjaxLink<Void> createExpandButton(final Component hidden) {
        return new AjaxLink<Void>("expandbutton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                hidden.setVisibilityAllowed(!hidden.isVisibleInHierarchy());
                target.add(hidden);
            }
        };
    }

}
