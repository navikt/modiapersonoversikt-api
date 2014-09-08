package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SVAR;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, MeldingVM meldingVM) {
        super(id);
        settBildeRessurs(meldingVM);
    }

    public final void settBildeRessurs(MeldingVM meldingVM) {
        String avsender = "", bilde = "";
        if (meldingVM != null) {
            if (meldingVM.melding.meldingstype == SVAR || meldingVM.melding.meldingstype == SAMTALEREFERAT) {
                avsender = "nav";
                bilde = "nav-logo.svg";
            } else if (meldingVM.melding.meldingstype == SPORSMAL) {
                avsender = "bruker";
                bilde = "siluett.svg";
            }
        }
        add(new AttributeModifier("src", WebApplication.get().getServletContext().getContextPath() + "/img/" + bilde));
        add(new AttributeModifier("alt", new StringResourceModel("innboks.avsender." + avsender, this, null)));
    }
}
