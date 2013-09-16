package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class TomtForOppgaverPanel extends Panel {

    public TomtForOppgaverPanel(String id, final ModigModalWindow modalWindow) {
        super(id);
        add(new Label("tekst", "Dere har jobbet på så bra at det ikke finnes noen fler oppgaver igjen på dette tema! Vennligst velg et annet tema!"));
        add(new AjaxLink<Void>("lukk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                modalWindow.close(target);
            }
        });
    }
}
