package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import java.text.NumberFormat;
import java.util.Locale;

public class UtbetalingPanel extends Panel {
    public UtbetalingPanel(String id, Utbetaling utbetaling) {
        super(id);
        String utbetalingsDato = Optional.optional(utbetaling.getUtbetalingsDato()).map(Datoformat.KORT).getOrElse("Ingen utbetalingsdato");
        String startDato =Optional.optional(utbetaling.getStartDate()).map(Datoformat.KORT).getOrElse("");
        String sluttDato =Optional.optional(utbetaling.getEndDate()).map(Datoformat.KORT).getOrElse("");

        String periode = startDato + " - " + sluttDato;


        NumberFormat currencyInstance = NumberFormat.getNumberInstance(Locale.forLanguageTag("nb-no"));
        currencyInstance.setMinimumFractionDigits(2);
        String nettoBelop = currencyInstance.format( utbetaling.getNettoBelop()) + " " + utbetaling.getValuta();



        add(new Label("beskrivelse", utbetaling.getBeskrivelse()));
        add(new Label("utbetalingdato", utbetalingsDato));
        add(new Label("belop", nettoBelop));
        add(new Label("periode", periode));
        add(new Label("status", utbetaling.getStatuskode()));
        add(new Label("kontonr", utbetaling.getKontoNr()));


    }
}
