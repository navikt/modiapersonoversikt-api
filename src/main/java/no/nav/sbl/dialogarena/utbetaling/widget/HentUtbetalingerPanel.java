package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.panels.GenericListingPanel;
import no.nav.sbl.dialogarena.utbetaling.util.AjaxIndicator;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class HentUtbetalingerPanel extends GenericListingPanel {

    public HentUtbetalingerPanel(final UtbetalingWidget widget) {
        add(new AjaxIndicator.SnurrepippAjaxLink("hentUtbetalingerLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                widget.hentUtbetalinger();
                target.add(widget);
            }
        });
    }
}
