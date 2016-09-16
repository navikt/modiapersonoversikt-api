package no.nav.sbl.dialogarena.sporsmalogsvar.common.components;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class StatusIkon extends Panel {

    public StatusIkon(String id, MeldingVM meldingVM) {
        this(id, false, meldingVM);
    }

    public StatusIkon(String id, boolean underBehandling, MeldingVM meldingVM) {
        super(id);
        WebMarkupContainer statusIkon = new WebMarkupContainer("statusIkon");

        int traadlengde = meldingVM.traadlengde;

        String besvartStatusKey = format("innboks.melding.%s", (meldingVM.erBesvart().getObject()) ? "besvart" : "ubesvart");

        String besvartStatus = new StringResourceModel(besvartStatusKey, this, null).getString();
        String antallMeldinger = format("%d %s",
                traadlengde,
                traadlengde == 1 ? "melding" : "meldinger"
        );

        String antallMeldingerTekst = "";

        if (traadlengde > 1) {
            if (traadlengde < 10) {
                antallMeldingerTekst = traadlengde + "";
            } else {
                antallMeldingerTekst = "9+";
            }
        }

        Label antallMeldingerIkonTekst = new Label("antallMeldingerIkonTekst", antallMeldingerTekst);

        Label statusIkonTekst = new Label("statusIkonTekst", format("%s%s, %s, ",
                underBehandling ? "Under behandling, " : "",
                besvartStatus,
                antallMeldinger));


        if (meldingVM.erDokumentMelding) {
            statusIkon.add(hasCssClassIf("dokument", meldingVM.erDokumentMelding()));
        } else if (meldingVM.erOppgaveMelding) {
            statusIkon.add(hasCssClassIf("oppgave", meldingVM.erOppgaveMelding()));
        } else {
            if (traadlengde > 1) {
                statusIkon.add(new AttributeAppender("class", " flere-meldinger"));
            } else {
                statusIkon.add(new AttributeAppender("class", " en-melding"));
            }
            statusIkon.add(hasCssClassIf("ubesvart", not(meldingVM.erBesvart())));
            statusIkon.add(hasCssClassIf("besvart", meldingVM.erBesvart()));
        }

        add(statusIkon, antallMeldingerIkonTekst, statusIkonTekst);
    }
}
