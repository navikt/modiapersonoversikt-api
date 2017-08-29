package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.print.PrintLenke;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class HaandterMeldingValgPanel extends Panel {

    static final String PANEL_TOGGLET = "sos.haandtermelding.panelTogglet";
    static final String PANEL_LUKKET = "sos.haandtermelding.panelLukket";

    public HaandterMeldingValgPanel(String id, final InnboksVM innboksVM, MeldingActionPanel meldingActionPanel) {
        super(id, new PropertyModel(innboksVM, "valgtTraad"));

        IModel<Boolean> erKontorsperret = new PropertyModel<>(getDefaultModel(), "erKontorsperret()");
        IModel<Boolean> erFeilsendt = new PropertyModel<>(getDefaultModel(), "erFeilsendt()");
        IModel<Boolean> nyesteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "nyesteMelding.journalfort");
        IModel<Boolean> eldsteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "eldsteMelding.journalfort");
        IModel<Boolean> erBehandlet = new PropertyModel<>(getDefaultModel(), "erBehandlet()");
        IModel<Boolean> erTemagruppeSosialeTjenester = new PropertyModel<>(getDefaultModel(), "erTemagruppeSosialeTjenester()");
        IModel<Boolean> erSporsmal = new PropertyModel<>(getDefaultModel(), "erMeldingstypeSporsmal()");
        IModel<Boolean> skalViseStandardMerkValg = both(not(eldsteMeldingErJournalfort)).and(not(erFeilsendt)).and(not(erSporsmal)).and(not(erKontorsperret));
        IModel<Boolean> skalViseFerdigstillUtenSvarValg = both(erSporsmal).and(not(erKontorsperret)).and(not(erBehandlet));


        add(new MeldingValgPanel("journalforingValg",
                both(not(erKontorsperret))
                        .and(not(erFeilsendt))
                        .and(not(nyesteMeldingErJournalfort))
                        .and(erBehandlet)
                        .and(not(erTemagruppeSosialeTjenester)),
                meldingActionPanel.journalforPanel));

        add(new MeldingValgPanel("nyoppgaveValg", erBehandlet, meldingActionPanel.oppgavePanel));

        add(new MeldingValgPanel("merkeValg", either(skalViseFerdigstillUtenSvarValg).or(skalViseStandardMerkValg), meldingActionPanel.merkePanel));

        PrintLenke printLenke = new PrintLenke("print", new PropertyModel<>(innboksVM, "valgtTraad.meldinger"));
        add(printLenke);

    }
}

