package no.nav.sbl.dialogarena.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.apache.wicket.markup.html.WebPage;

public class UtbetalingTestPage extends WebPage {

    public UtbetalingTestPage() {
        String fnr = "12345612345";
        add(new UtbetalingLerret("utbetaling", fnr));
        add(new UtbetalingWidget("utbetalingWidget", "U", fnr));
    }

}
