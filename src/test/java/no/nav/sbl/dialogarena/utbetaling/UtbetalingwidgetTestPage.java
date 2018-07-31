package no.nav.sbl.dialogarena.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget;
import org.apache.wicket.markup.html.WebPage;

public class UtbetalingwidgetTestPage extends WebPage {

    public static final String AREMARK_FNR = "10108000398";

    public UtbetalingwidgetTestPage() {
        add(new UtbetalingWidget("utbetalingWidget", "U", AREMARK_FNR));
    }
}