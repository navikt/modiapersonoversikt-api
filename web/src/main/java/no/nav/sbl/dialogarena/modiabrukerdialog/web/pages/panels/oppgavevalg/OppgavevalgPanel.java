package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class OppgavevalgPanel extends Panel {

    public OppgavevalgPanel(String id, final String oppgaveId) {
        super(id);
        final LeggTilbakeForm leggTilbakeForm = new LeggTilbakeForm("legg-tilbake-form", oppgaveId);
        leggTilbakeForm.setVisible(false);
        leggTilbakeForm.setOutputMarkupPlaceholderTag(true);

        final WebMarkupContainer valg = new WebMarkupContainer("oppgavevalg-liste");
        valg.add(new AjaxLink<Void>("legg-tilbake-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                valg.setVisible(false);
                leggTilbakeForm.setVisible(true);
                target.add(valg, leggTilbakeForm);


            }
        });

        valg.setVisible(false);
        valg.setOutputMarkupPlaceholderTag(true);

        add(valg, leggTilbakeForm, new AjaxLink<Void>("oppgavevalg-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                valg.setVisible(!valg.isVisible());
                leggTilbakeForm.setVisible(false);
                target.add(valg, leggTilbakeForm);
            }
        });
    }


}
