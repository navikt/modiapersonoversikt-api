package no.nav.sbl.dialogarena.utbetaling.lamell.unntak;

import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.util.AjaxIndicator;

/**
 * Panel for feilmelding som inneholder en prøv-igjen lenke
 */
public class FeilmeldingPanel extends UtbetalingerMessagePanel {

    private final AjaxIndicator.SnurrepippAjaxEventLink snurrepippAjaxEventLink;

    public FeilmeldingPanel(String id, String messageKey, String cssClass) {
        super(id, messageKey, cssClass);
        snurrepippAjaxEventLink = new AjaxIndicator.SnurrepippAjaxEventLink("prov-igjen", FilterParametere.FILTER_ENDRET);
        add(snurrepippAjaxEventLink);
    }

    public void endreLenkeSynlighet(boolean synlig) {
        snurrepippAjaxEventLink.setVisibilityAllowed(synlig);
    }
}
