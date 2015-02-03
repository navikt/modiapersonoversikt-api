package no.nav.sbl.dialogarena.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class UtbetalingTestPage extends WebPage {

    public UtbetalingTestPage(final PageParameters pageParameters) {
        String fnr = "12345612345";
        StringValue paramFnr = pageParameters.get("fnr");
        if(!paramFnr.isNull() && !paramFnr.isEmpty()) {
            fnr = paramFnr.toString();
        }

        add(new BookmarkablePageLink<WebPage>("widget-link", UtbetalingwidgetTestPage.class));
        add(new UtbetalingLerret("utbetaling", fnr));
    }

}
