package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class UnderytelsePanel extends Panel {

    public UnderytelsePanel(String id, Underytelse underytelse) {
        super(id);
        add(
                new Label("ytelse", underytelse.getTittel()),
                new Label("antall", underytelse.getAntall()),
                new Label("sats", underytelse.getSats()),
                new Label("belop", underytelse.getBelop())
        );
    }
}
