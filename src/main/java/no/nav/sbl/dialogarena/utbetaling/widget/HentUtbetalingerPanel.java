package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.panels.GenericListingPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public class HentUtbetalingerPanel extends GenericListingPanel {
    public HentUtbetalingerPanel(final UtbetalingWidget widget) {
        add(new AjaxLink("hentUtbetalingerLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                widget.hentUtbetalinger();
                target.add(widget);
            }
        });
    }
}
