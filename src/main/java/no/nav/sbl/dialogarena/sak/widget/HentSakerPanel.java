package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.modia.widget.panels.GenericListingPanel;
import no.nav.sbl.dialogarena.sak.util.AjaxIndicator;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class HentSakerPanel extends GenericListingPanel {

    public HentSakerPanel(final SaksWidget widget) {
        add(new AjaxIndicator.SnurrepippAjaxLink("hentSakerLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                widget.hentSaker();
                target.add(widget);
            }
        });
    }
}
