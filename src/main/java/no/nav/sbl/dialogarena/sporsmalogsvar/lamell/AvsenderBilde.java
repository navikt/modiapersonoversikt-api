package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.ContextRelativeResource;

import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.UTGAENDE;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, MeldingVM meldingVM) {
        super(id);
        settBildeRessurs(meldingVM);
    }

    public void settBildeRessurs(MeldingVM meldingVM) {
        String avsender = "", bilde = "";
        if (meldingVM.melding.meldingstype == UTGAENDE) {
            avsender = "nav";
            bilde = "nav-logo.svg";
        } else if (meldingVM.melding.meldingstype == INNGAENDE) {
            avsender = "bruker";
            bilde = "siluett.svg";
        }
        setImageResource(new ContextRelativeResource("img/" + bilde));
        add(new AttributeModifier("alt", new StringResourceModel("innboks.avsender." + avsender, this, null)));
    }
}
