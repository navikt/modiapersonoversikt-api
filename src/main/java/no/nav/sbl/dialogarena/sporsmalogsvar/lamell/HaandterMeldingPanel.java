package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.modia.events.InternalEvents.SVAR_PAA_MELDING;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class HaandterMeldingPanel extends Panel {

    public HaandterMeldingPanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);

        AjaxLink<InnboksVM> besvar = new AjaxLink<InnboksVM>("besvar", innboksVM) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(SVAR_PAA_MELDING, getModelObject().getNyesteMelding().melding.id));
            }
        };

        besvar.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getObject().valgtMeldingErEtUbesvartSporsmaal();
            }
        }));

        add(besvar);
    }
}
