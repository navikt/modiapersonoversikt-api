package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.AnimertJournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave.OppgavePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.print.PrintLenke;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class HaandterMeldingPanel extends Panel {

    static final String PANEL_TOGGLET = "sos.haandtermelding.panelTogglet";
    static final String PANEL_LUKKET = "sos.haandtermelding.panelLukket";

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id, new PropertyModel(innboksVM, "valgtTraad"));

        IModel<Boolean> erKontorsperret = new PropertyModel<>(getDefaultModel(), "erKontorsperret()");
        IModel<Boolean> erFeilsendt = new PropertyModel<>(getDefaultModel(), "erFeilsendt()");
        IModel<Boolean> nyesteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "nyesteMelding.journalfort");
        IModel<Boolean> eldsteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "eldsteMelding.journalfort");
        IModel<Boolean> erBehandlet = new PropertyModel<>(getDefaultModel(), "erBehandlet()");

        AnimertJournalforingsPanel journalforingsPanel = new AnimertJournalforingsPanel("journalforPanel", innboksVM);
        add(journalforingsPanel);
        add(new MeldingValgPanel("journalforingValg", both(not(erKontorsperret)).and(not(erFeilsendt)).and(not(nyesteMeldingErJournalfort)).and(erBehandlet), journalforingsPanel));

        OppgavePanel oppgavePanel = new OppgavePanel("nyoppgavePanel", innboksVM);
        add(oppgavePanel);
        add(new MeldingValgPanel("nyoppgaveValg", erBehandlet, oppgavePanel));

        MerkePanel merkePanel = new MerkePanel("merkePanel", innboksVM);
        add(merkePanel);
        add(new MeldingValgPanel("merkeValg", both(not(eldsteMeldingErJournalfort)).and(erBehandlet).and(not(erFeilsendt)), merkePanel));

        PrintLenke printLenke = new PrintLenke("print", new PropertyModel<List<MeldingVM>>(innboksVM, "valgtTraad.meldinger"));
        add(printLenke);

    }
}

