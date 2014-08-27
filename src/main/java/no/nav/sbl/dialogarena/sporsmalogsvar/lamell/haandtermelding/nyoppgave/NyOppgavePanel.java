package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class NyOppgavePanel extends AnimertPanel {

    public NyOppgavePanel(String id, final InnboksVM innboksVM) {
        super(id);

        add(new Label("temagruppe", new StringResourceModel("${temagruppe}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding.melding"))));

        add(new NyOppgaveFormWrapper("nyoppgave-form", innboksVM) {
            @Override
            protected void etterSubmit(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        });

        add(new AjaxLink<Void>("avbrytlink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        });
    }
}
