package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class UtbetalingPanel extends Panel {
    public UtbetalingPanel(String id, Utbetaling utbetaling) {
        super(id);
        add(new Label("beskrivelse", utbetaling.getBeskrivelse()));

    }
}
