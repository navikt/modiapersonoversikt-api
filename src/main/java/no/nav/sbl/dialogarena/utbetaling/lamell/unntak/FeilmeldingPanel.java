package no.nav.sbl.dialogarena.utbetaling.lamell.unntak;

import no.nav.modig.wicket.events.components.AjaxEventLink;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;

/**
 * Panel for feilmelding som inneholder en prøv-igjen lenke
 */
public class FeilmeldingPanel extends UtbetalingerMessagePanel {

    public FeilmeldingPanel(String id, String messageKey, String cssClass) {
        super(id, messageKey, cssClass);
        add(new AjaxEventLink<>("prov-igjen", FilterParametere.ENDRET));
    }
}
