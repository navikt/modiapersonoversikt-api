package no.nav.sbl.dialogarena.sporsmalogsvar.common.components;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class StatusIkon extends Panel {

    public StatusIkon(String id, MeldingVM meldingVM) {
        this(id, false, false, meldingVM);
    }

    public StatusIkon(String id, boolean underBehandling, boolean erValgt, MeldingVM meldingVM) {
        super(id);
        WebMarkupContainer statusIkon = new WebMarkupContainer("statusIkon");

        String besvartStatusKey = format("innboks.melding.%s", (meldingVM.erBesvart().getObject()) ? "besvart" : "ubesvart");

        String besvartStatus = new StringResourceModel(besvartStatusKey, this, null).getString();
        String antallMeldinger = format("%d %s",
                meldingVM.traadlengde,
                meldingVM.traadlengde == 1 ? "melding" : "meldinger"
        );
        String dato = meldingVM.getAvsenderDato();
        String typeMelding = new PropertyModel<String>(meldingVM, "melding.statusTekst").getObject();
        String tema = new PropertyModel<String>(meldingVM, "melding.temagruppeNavn").getObject();

        Label statusIkonTekst = new Label("statusIkonTekst", format("%s%s%s, %s",
                erValgt ? "Valgt, " : "",
                underBehandling ? "Under behandling, " : "",
                besvartStatus,
                antallMeldinger));


        statusIkon.add(hasCssClassIf("ubesvart", not(meldingVM.erBesvart())));
        statusIkon.add(hasCssClassIf("besvart", meldingVM.erBesvart()));

        add(statusIkon, statusIkonTekst);
    }
}
