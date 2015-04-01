package no.nav.sbl.dialogarena.sporsmalogsvar.common.components;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class StatusIkon extends Panel {

    public StatusIkon(String id, MeldingVM meldingVM) {
        super(id);
        WebMarkupContainer statusIkon = new WebMarkupContainer("statusIkon");
        Label statusIkonTekst = new Label("statusIkonTekst", new StringResourceModel(
                String.format("innboks.melding.%s", (meldingVM.erBesvart().getObject()) ? "besvart" : "ubesvart"), this, null)
        );


        statusIkon.add(hasCssClassIf("ubesvart", not(meldingVM.erBesvart())));
        statusIkon.add(hasCssClassIf("besvart", meldingVM.erBesvart()));

        add(statusIkon, statusIkonTekst);
    }
}
