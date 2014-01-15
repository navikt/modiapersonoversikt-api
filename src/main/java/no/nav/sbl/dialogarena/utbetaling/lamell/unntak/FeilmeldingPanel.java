package no.nav.sbl.dialogarena.utbetaling.lamell.unntak;

import no.nav.modig.wicket.events.components.AjaxEventLink;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere;
import org.apache.wicket.ajax.IAjaxIndicatorAware;

/**
 * Panel for feilmelding som inneholder en prøv-igjen lenke
 */
public class FeilmeldingPanel extends UtbetalingerMessagePanel {

    public FeilmeldingPanel(String id, String messageKey, String cssClass) {
        super(id, messageKey, cssClass);
        add(new IndicatingAjaxEventLink("prov-igjen", FilterParametere.FILTER_ENDRET));
    }

    private static class IndicatingAjaxEventLink extends AjaxEventLink implements IAjaxIndicatorAware {
        public IndicatingAjaxEventLink(String id, String eventName) {
            super(id, eventName);
        }

        @Override
        public String getAjaxIndicatorMarkupId() {
            return "ajax-indikator";
        }
    }
}
