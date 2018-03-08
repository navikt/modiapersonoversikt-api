package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, IModel<MeldingVM> meldingVM) {
        super(id);

        add(new AttributeModifier("src", new PropertyModel<String>(meldingVM, "avsenderBildeUrl")));
        add(new AttributeModifier("alt", new StringResourceModel("${avsenderBildeAltKey}", meldingVM)));
    }

}
