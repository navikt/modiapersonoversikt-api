package no.nav.sbl.dialogarena.utbetaling.lamell;

import java.text.NumberFormat;
import java.util.List;
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

import static java.text.NumberFormat.getNumberInstance;
import static java.util.Locale.forLanguageTag;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, Utbetaling utbetaling) {
        super(id);
        ListView<Bilag> bilagListView = createBilagListView(utbetaling.getBilag());
        // En ListView kan ikke addes direkte via Ajax. Den må derfor legges i en wrapper.
        WebMarkupContainer bilagWrapper = new WebMarkupContainer("bilag-wrapper");
        bilagWrapper.add(bilagListView);
        bilagWrapper.setOutputMarkupPlaceholderTag(true);
        bilagWrapper.setVisibilityAllowed(false);

        add(
                bilagWrapper,
                new Label("beskrivelse", utbetaling.getBeskrivelse()),
                createUtbetalingDatoLabel(utbetaling),
                createBelopLabel(utbetaling),
                createPeriodeLabel(utbetaling),
                new Label("status", utbetaling.getStatuskode()),
                new Label("kontonr", utbetaling.getKontoNr()),
                new Label("mottaker", utbetaling.getMottaker().getNavn()),
                createExpandButton(bilagWrapper)
        );
    }

    private Label createUtbetalingDatoLabel(Utbetaling utbetaling) {
        return new Label("utbetalingdato", optional(utbetaling.getUtbetalingsDato()).map(KORT).getOrElse("Ingen utbetalingsdato"));
    }

    private ListView<Bilag> createBilagListView(List<Bilag> bilagListe) {
        return new ListView<Bilag>("bilag-liste", bilagListe) {
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
                item.add(new Label("kontonr", posteringsDetalj.getKontoNr()));
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

    private Label createPeriodeLabel(Utbetaling utbetaling) {
        return new Label("periode", optional(utbetaling.getStartDate()).map(KORT).getOrElse("") + " - " + optional(utbetaling.getEndDate()).map(KORT).getOrElse(""));
    }

    private Label createBelopLabel(Utbetaling utbetaling) {
        NumberFormat currencyInstance = getNumberInstance(forLanguageTag("nb-no"));
        currencyInstance.setMinimumFractionDigits(2);
        return new Label("belop", currencyInstance.format(utbetaling.getNettoBelop()) + " " + utbetaling.getValuta());
    }

}
