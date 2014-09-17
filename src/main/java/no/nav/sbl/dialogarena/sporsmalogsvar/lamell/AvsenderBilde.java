package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, MeldingVM meldingVM) {
        super(id);
        settBildeRessurs(meldingVM);
    }

    public final void settBildeRessurs(MeldingVM meldingVM) {
        String avsender = "", bilde = "";
        if (meldingVM != null) {
            if (SVAR.contains(meldingVM.melding.meldingstype)|| SAMTALEREFERAT.contains(meldingVM.melding.meldingstype)) {
                avsender = "nav";
                bilde = "nav-logo.svg";
            } else if (meldingVM.melding.meldingstype == SPORSMAL_SKRIFTLIG) {
                avsender = "bruker";
                bilde = "siluett.svg";
            }
        }
        add(new AttributeModifier("src", WebApplication.get().getServletContext().getContextPath() + "/img/" + bilde));
        add(new AttributeModifier("alt", new StringResourceModel("innboks.avsender." + avsender, this, null)));
    }
}
