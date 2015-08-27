package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.print.PrintLenke;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

import static no.nav.modig.wicket.model.ModelUtils.both;
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

        add(new MeldingValgPanel("reactJournalforingValg", Model.of(true), meldingActionPanel.journalforPanel2));
        add(new MeldingValgPanel("journalforingValg",
                both(not(erKontorsperret))
                        .and(not(erFeilsendt))
                        .and(not(nyesteMeldingErJournalfort))
                        .and(erBehandlet)
                        .and(not(erTemagruppeSosialeTjenester)),
                meldingActionPanel.journalforPanel));

        add(new MeldingValgPanel("nyoppgaveValg", erBehandlet, meldingActionPanel.oppgavePanel));

        add(new MeldingValgPanel("merkeValg", both(not(eldsteMeldingErJournalfort)).and(erBehandlet).and(not(erFeilsendt)), meldingActionPanel.merkePanel));

        PrintLenke printLenke = new PrintLenke("print", new PropertyModel<List<MeldingVM>>(innboksVM, "valgtTraad.meldinger"));
        add(printLenke);

    }
}

