package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public abstract class SokKnapp<T> extends AjaxLink<T> {

    public SokKnapp(String id) {
        super(id);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.BUBBLE);
    }
}
