package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, Utbetaling utbetaling) {
        super(id);
        add(
                new Label("mottatt", utbetaling.getMottaker().getNavn()),
                new Label("konto", utbetaling.getKontoNr()),
                new Label("ytelsesinfo", utbetaling.getBeskrivelse()),
                new Label("periode", utbetaling.getPeriodeMedKortDato()),
                createBilagListView(utbetaling.getBilag())
        );
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
}
