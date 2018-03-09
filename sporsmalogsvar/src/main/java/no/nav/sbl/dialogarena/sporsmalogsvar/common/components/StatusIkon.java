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

        int traadLengde = kalkulerTraadlengde(meldingVM);
        String antallMeldinger = lagAntallMeldingerTekst(traadLengde);
        Label antallMeldingerTekst = new Label("antallMeldingerIkonTekst", antallMeldinger);
        Label statusIkonTekst = new Label("statusIkonTekst", lagStatustekst(meldingVM, underBehandling,
                traadLengde, erMonolog));

        addStyles(meldingVM, erMonolog);
        add(ikon, antallMeldingerTekst, statusIkonTekst);
    }

    private int kalkulerTraadlengde(MeldingVM meldingVM) {
        int traadLengde = meldingVM.traadlengde;
        if (meldingVM.erFerdigstiltUtenSvar()) {
            traadLengde++;
        }
        return traadLengde;
    }

    private String lagIkonTekst(MeldingVM meldingVM) {
        String key;

        switch (meldingVM.getMeldingstype()) {
            case SAMTALEREFERAT_OPPMOTE:
                key = "oppmote";
                break;
            case SAMTALEREFERAT_TELEFON:
                key = "telefon";
                break;
            case OPPGAVE_VARSEL:
                key = "oppgave";
                break;
            case DOKUMENT_VARSEL:
                key = "dokument";
                break;
            case SVAR_OPPMOTE:
            case SVAR_TELEFON:
            case SPORSMAL_MODIA_UTGAAENDE:
                key = "sporsmal";
                break;
            default:
                key = lagDefaultIkonTekst(meldingVM);
        }

        return new StringResourceModel("innboks.melding." + key, this, null).getString();
    }

    private String lagDefaultIkonTekst(MeldingVM meldingVM) {
        if (meldingVM.erFerdigstiltUtenSvar() || meldingVM.erBesvart().getObject()) {
            return "besvart";
        }
        return "ubesvart";
    }

    private String lagStatustekst(MeldingVM meldingVM, boolean underBehandling, int traadlengde, boolean erMonolog) {
        String ikontekst = lagIkonTekst(meldingVM);
        String antallMeldinger = format("%d %s", traadlengde, traadlengde == 1 ? "melding" : "meldinger");
        return format("%s%s, %s, ", underBehandling ? "Under behandling, " : "", ikontekst, antallMeldinger);
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
                if(meldingVM.erFerdigstiltUtenSvar()) {
                    addCSSClass("dialog");
                    addCSSClass("besvart");
                } else if (erMonolog) {
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
