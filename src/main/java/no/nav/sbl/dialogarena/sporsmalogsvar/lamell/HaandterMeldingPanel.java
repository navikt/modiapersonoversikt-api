package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.JournalforingsPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.nyoppgave.NyOppgavePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class HaandterMeldingPanel extends Panel {

    public HaandterMeldingPanel(String id, final InnboksVM innboksVM) {
        super(id);

        final JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforingsPanel", innboksVM);
        journalforingsPanel.setVisibilityAllowed(false);
        final NyOppgavePanel nyOppgavePanel = new NyOppgavePanel("nyOppgavePanel", innboksVM);
        nyOppgavePanel.setVisibilityAllowed(false);

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
                journalforingsPanel.apneJournalforingsPanel(target);
                nyOppgavePanel.lukkNyOppgavePanel(target);
            }
        };
        journalforLink.add(enabledIf(not(new PropertyModel<Boolean>(innboksVM, "valgtTraad.nyesteMelding.nyesteMeldingISinJournalfortgruppe"))));

        AjaxLink nyOppgaveLink = new AjaxLink("nyoppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalforingsPanel.lukkJournalforingsPanel(target);
                nyOppgavePanel.apneNyOppgavePanel(target);
            }
        };
        nyOppgaveLink.add(enabledIf(new PropertyModel<Boolean>(innboksVM, "valgtTraad.erBehandlet()")));

        add(besvarLink, journalforLink, nyOppgaveLink, journalforingsPanel, nyOppgavePanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forReference(new JavaScriptResourceReference(HaandterMeldingPanel.class, "haandtermelding.js")));
    }
}
