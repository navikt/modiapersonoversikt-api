package no.nav.sbl.dialogarena.sporsmalogsvar.common.components;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.widget.WidgetMeldingVM;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class StatusIkon extends Panel {

    private WebMarkupContainer ikon;

    public StatusIkon(String id, WidgetMeldingVM meldingVM) {
        this(id, meldingVM, meldingVM.erMonolog, false);
    }

    public StatusIkon(String id, TraadVM traadVM, boolean underBehandling) {
        this(id, traadVM.getNyesteMelding(), traadVM.erMonolog(), underBehandling);
    }

    private StatusIkon(String id, MeldingVM meldingVM, boolean erMonolog, boolean underBehandling) {
        super(id);
        ikon = new WebMarkupContainer("statusIkon");

        int traadlengde = meldingVM.traadlengde;
        Label antallMeldingerTekst = new Label("antallMeldingerIkonTekst", lagAntallMeldingerTekst(traadlengde));
        Label statusIkonTekst = new Label("statusIkonTekst", lagStatustekst(meldingVM, underBehandling, traadlengde));

        addStyles(meldingVM, erMonolog);

        add(ikon, antallMeldingerTekst, statusIkonTekst);
    }

    private String lagStatustekst(MeldingVM meldingVM, boolean underBehandling, int traadlengde) {
        String besvartStatusKey = format("innboks.melding.%s", meldingVM.erBesvart().getObject() ? "besvart" : "ubesvart");
        String besvartStatus = new StringResourceModel(besvartStatusKey, this, null).getString();
        String antallMeldinger = format("%d %s", traadlengde, traadlengde == 1 ? "melding" : "meldinger");
        return format("%s%s, %s, ", underBehandling ? "Under behandling, " : "", besvartStatus, antallMeldinger);
    }

    private String lagAntallMeldingerTekst(int traadlengde) {
        if (traadlengde < 2)
            return "";
        return traadlengde < 10 ? String.valueOf(traadlengde) : "9+";
    }

    private void addStyles(MeldingVM meldingVM, boolean erMonolog) {
        switch (meldingVM.getMeldingstype()) {
            case SAMTALEREFERAT_OPPMOTE:
                addCSSClass("oppmote");
                break;
            case SAMTALEREFERAT_TELEFON:
                addCSSClass("telefon");
                break;
            case OPPGAVE_VARSEL:
                addCSSClass("oppgave");
                break;
            case DOKUMENT_VARSEL:
                addCSSClass("dokument");
                break;
            default:
                if (erMonolog) {
                    addCSSClass("monolog");
                    ikon.add(hasCssClassIf("ubesvart", Model.of(!meldingVM.erFraSaksbehandler())));
                } else {
                    addCSSClass("dialog");
                    ikon.add(hasCssClassIf("besvart", meldingVM.erBesvart()));
                }
        }
    }

    private void addCSSClass(String cssClass) {
        ikon.add(new AttributeAppender("class", " " + cssClass));
    }

}
