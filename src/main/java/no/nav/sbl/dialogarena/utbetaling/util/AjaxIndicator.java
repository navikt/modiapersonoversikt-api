package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.modig.wicket.events.components.AjaxEventLink;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;

public class AjaxIndicator {

    private static final String AJAX_INDIKATOR_ID = "ajax-indikator";

    public static abstract class SnurrepippAjaxLink extends AjaxLink implements IAjaxIndicatorAware {

        public SnurrepippAjaxLink(String id) {
            super(id);
        }

        @Override
        public String getAjaxIndicatorMarkupId() {
            return AJAX_INDIKATOR_ID;
        }
    }

    public static class SnurrepippAjaxEventLink extends AjaxEventLink implements IAjaxIndicatorAware {
        public SnurrepippAjaxEventLink(String id, String eventName) {
            super(id, eventName);
        }

        @Override
        public String getAjaxIndicatorMarkupId() {
            return AJAX_INDIKATOR_ID;
        }
    }

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
