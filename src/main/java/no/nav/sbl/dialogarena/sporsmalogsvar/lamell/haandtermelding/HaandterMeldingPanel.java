package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave.NyOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.wicket.event.Broadcast.EXACT;

public class HaandterMeldingPanel extends Panel {

    static final String PANEL_TOGGLET = "sos.haandtermelding.panelTogglet";
    static final String PANEL_LUKKET = "sos.haandtermelding.panelLukket";

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id, new PropertyModel(innboksVM, "valgtTraad"));

        IModel<Boolean> erKontorsperret = new PropertyModel<>(getDefaultModel(), "erKontorsperret()");
        IModel<Boolean> nyesteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "nyesteMelding.journalfort");
        IModel<Boolean> eldsteMeldingErJournalfort = new PropertyModel<>(getDefaultModel(), "eldsteMelding.journalfort");
        IModel<Boolean> erBehandlet = new PropertyModel<>(getDefaultModel(), "erBehandlet()");
        IModel<Boolean> bleInitiertAvBruker = new PropertyModel<>(getDefaultModel(), "bleInitiertAvBruker()");

        add(
                new AjaxLink<InnboksVM>("besvar") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        send(getPage(), EXACT, new NamedEventPayload(SVAR_PAA_MELDING, innboksVM.getValgtTraad().getEldsteMelding().melding.id));
                    }
                }.add(enabledIf(bleInitiertAvBruker))
        );

        JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalfor-panel", innboksVM);
        add(journalforingsPanel);
        add(new MeldingValgPanel("journalforingValg", both(not(erKontorsperret)).and(not(nyesteMeldingErJournalfort)), journalforingsPanel));

        NyOppgavePanel nyOppgavePanel = new NyOppgavePanel("nyoppgave-panel", innboksVM);
        add(nyOppgavePanel);
        add(new MeldingValgPanel("nyoppgaveValg", erBehandlet, nyOppgavePanel));

        MerkePanel merkePanel = new MerkePanel("merke-panel", innboksVM);
        add(merkePanel);
        add(new MeldingValgPanel("merkeValg", both(not(eldsteMeldingErJournalfort)).and(not(erKontorsperret)).and(erBehandlet), merkePanel));
    }

}
