package no.nav.sbl.dialogarena.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.apache.wicket.markup.html.WebPage;

public class UtbetalingwidgetTestPage extends WebPage {

    public UtbetalingwidgetTestPage() {
        String fnr = "***REMOVED***";
        add(new UtbetalingWidget("utbetalingWidget", "U", fnr));
    }

}
