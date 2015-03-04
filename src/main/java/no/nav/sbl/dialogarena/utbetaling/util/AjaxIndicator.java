package no.nav.sbl.dialogarena.utbetaling.util;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.markup.html.form.Form;

public class AjaxIndicator {

    private static final String AJAX_INDIKATOR_ID = "ajax-indikator";

    public static class SnurrepippFilterForm<T> extends Form<T> implements IAjaxIndicatorAware {
        public SnurrepippFilterForm(String id) {
            super(id);
        }

        @Override
        public String getAjaxIndicatorMarkupId() {
            return AJAX_INDIKATOR_ID;
        }
    }
}
