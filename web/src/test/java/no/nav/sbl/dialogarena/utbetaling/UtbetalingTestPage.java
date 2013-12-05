package no.nav.sbl.dialogarena.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class UtbetalingTestPage extends WebPage {

    public UtbetalingTestPage() {
        String fnr = "12345612345";
        add(new BookmarkablePageLink<WebPage>("widget-link", UtbetalingwidgetTestPage.class));
        add(new UtbetalingLerret("utbetaling", fnr));
    }

}
