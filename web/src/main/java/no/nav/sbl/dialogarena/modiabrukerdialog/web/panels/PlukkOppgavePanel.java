package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class PlukkOppgavePanel extends Panel {

    public PlukkOppgavePanel(String id) {
        super(id);
        AjaxLink<Void> velgTemagruppe = new AjaxLink<Void>("velg-temagruppe") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
        WebMarkupContainer temagruppeListe = new WebMarkupContainer("temagruppe-liste");
        AjaxLink<Void> plukkOppgave = new AjaxLink<Void>("plukk-oppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
        add(velgTemagruppe, temagruppeListe, plukkOppgave);
    }
}
