package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave.NyOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class HaandterMeldingPanel extends Panel {

    private final List<AnimertPanel> meldingHaandteringsPaneler;

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id);

        final JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforingsPanel", innboksVM);
        journalforingsPanel.setVisibilityAllowed(false);
        final NyOppgavePanel nyOppgavePanel = new NyOppgavePanel("nyOppgavePanel", innboksVM);
        nyOppgavePanel.setVisibilityAllowed(false);

        meldingHaandteringsPaneler = asList(journalforingsPanel, nyOppgavePanel);

        AjaxLink<InnboksVM> besvarLink = new AjaxLink<InnboksVM>("besvar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(SVAR_PAA_MELDING, innboksVM.getValgtTraad().getEldsteMelding().melding.id));
            }
        };
        besvarLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.bleInitiertAvBruker()")));

        AjaxLink journalforLink = new AjaxLink("journalfor") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                togglePaneler(target, JournalforingsPanel.class);
            }
        };
        journalforLink.add(enabledIf(not(new PropertyModel<Boolean>(innboksVM, "valgtTraad.nyesteMelding.nyesteMeldingISinJournalfortgruppe"))));

        AjaxLink nyOppgaveLink = new AjaxLink("nyoppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                togglePaneler(target, NyOppgavePanel.class);
            }
        };
        nyOppgaveLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.erBehandlet()")));

        add(besvarLink, journalforLink, nyOppgaveLink, journalforingsPanel, nyOppgavePanel);
    }

    private void togglePaneler(AjaxRequestTarget target, Class synligPanelType) {
        for (AnimertPanel panel : meldingHaandteringsPaneler) {
            if (panel.getClass() == synligPanelType) {
                panel.togglePanel(target);
            } else {
                panel.lukkPanel(target);
            }
        }
    }
}
