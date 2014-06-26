package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing.JournalforingsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;

public class HaandterMeldingPanel extends Panel {

    public HaandterMeldingPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);

        AjaxLink<InnboksVM> besvar = new AjaxLink<InnboksVM>("besvar", innboksVM) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(SVAR_PAA_MELDING, getModelObject().getValgtTraad().getEldsteMelding().melding.id));
            }
        };

        besvar.add(enabledIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getObject().getValgtTraad().bleInitiertAvBruker();
            }
        }));

        final JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforingsPanel", innboksVM);
        journalforingsPanel.setVisibilityAllowed(false);

        final AjaxLink<InnboksVM> journalfor = new AjaxLink<InnboksVM>("journalfor") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalforingsPanel.setVisibilityAllowed(true);
                target.add(journalforingsPanel);
            }
        };

        add(besvar, journalfor, journalforingsPanel);
    }

}
