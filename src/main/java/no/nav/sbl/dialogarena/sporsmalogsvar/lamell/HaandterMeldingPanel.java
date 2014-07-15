package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.nyoppgave.NyOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class HaandterMeldingPanel extends Panel {

    public HaandterMeldingPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);

        final JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforingsPanel", innboksVM);
        journalforingsPanel.setVisibilityAllowed(false);
        final NyOppgavePanel nyOppgavePanel = new NyOppgavePanel("nyOppgavePanel");
        nyOppgavePanel.setVisibilityAllowed(false);

        AjaxLink<InnboksVM> besvarLink = new AjaxLink<InnboksVM>("besvar", innboksVM) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(SVAR_PAA_MELDING, getModelObject().getValgtTraad().getEldsteMelding().melding.id));
            }
        };
        besvarLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.bleInitiertAvBruker()")));

        AjaxLink journalforLink = new AjaxLink("journalfor") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalforingsPanel.setVisibilityAllowed(true);
                nyOppgavePanel.setVisibilityAllowed(false);

                journalforingsPanel.oppdatereJournalforingssaker();

                target.add(journalforingsPanel, nyOppgavePanel);
            }
        };
        journalforLink.add(enabledIf(not(new PropertyModel<Boolean>(innboksVM, "valgtTraad.nyesteMelding.nyesteMeldingISinJournalfortgruppe"))));

        AjaxLink nyOppgaveLink = new AjaxLink("nyoppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalforingsPanel.setVisibilityAllowed(false);
                nyOppgavePanel.setVisibilityAllowed(true);

                target.add(journalforingsPanel, nyOppgavePanel);
            }
        };
        nyOppgaveLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.erBehandlet()")));

        add(besvarLink, journalforLink, nyOppgaveLink, journalforingsPanel, nyOppgavePanel);
    }
}
