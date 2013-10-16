package no.nav.sbl.dialogarena.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLamell;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.apache.wicket.markup.html.WebPage;

public class UtbetalingTestPage extends WebPage {


    public UtbetalingTestPage() {
        add(new UtbetalingWidget("utbetalingWidget", "U", "12345612345"));
        add(new UtbetalingLamell("utbetaling"));
    }

}
