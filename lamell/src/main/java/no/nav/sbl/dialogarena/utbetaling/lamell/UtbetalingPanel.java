package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import java.text.NumberFormat;

import static java.text.NumberFormat.getNumberInstance;
import static java.util.Locale.forLanguageTag;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.time.Datoformat.KORT;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, Utbetaling utbetaling) {
        super(id);
        final Label hidden = createHiddenLabel();

        add(
                hidden,
                new Label("beskrivelse", utbetaling.getBeskrivelse()),
                createUtbetalingDatoLabel(utbetaling),
                createBelopLabel(utbetaling),
                createPeriodeLabel(utbetaling),
                new Label("status", utbetaling.getStatuskode()),
                new Label("kontonr", utbetaling.getKontoNr()),
                createExpandButton(hidden)
        );
    }

    private Label createUtbetalingDatoLabel(Utbetaling utbetaling) {
        return new Label("utbetalingdato", optional(utbetaling.getUtbetalingsDato()).map(KORT).getOrElse("Ingen utbetalingsdato"));
    }

    private Label createHiddenLabel() {
        return (Label) new Label("hidden", "SKJULT")
                .setVisible(false)
                .setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true);
    }

    private AjaxLink<Void> createExpandButton(final Label hidden) {
        return new AjaxLink<Void>("expandbutton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                hidden.setVisible(!hidden.isVisible());
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
