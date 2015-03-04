package no.nav.sbl.dialogarena.sporsmalogsvar.common.components;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class StatusIkon extends Image {

    public StatusIkon(String id, IModel<? extends MeldingVM> meldingVM) {
        super(id);

        add(new AttributeModifier("src", new PropertyModel<String>(meldingVM, "statusIkonUrl")));
        add(new AttributeModifier("alt", new StringResourceModel("${statusIkonAltKey}", meldingVM)));
    }
}
