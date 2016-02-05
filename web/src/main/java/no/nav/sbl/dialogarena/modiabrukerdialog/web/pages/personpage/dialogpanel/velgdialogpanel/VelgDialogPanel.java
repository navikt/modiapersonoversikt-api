package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.velgdialogpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_LENKE_VALGT;

public class VelgDialogPanel extends Panel {
    public VelgDialogPanel(String id) {
        super(id);

        AjaxLink startNyDialogLenke = new AjaxLink("startNyDialogLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, new NamedEventPayload(NY_DIALOG_LENKE_VALGT));
            }
        };
        startNyDialogLenke.setOutputMarkupId(true);
        add(startNyDialogLenke);

        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        if (target != null) {
            target.focusComponent(startNyDialogLenke);
        }

    }
}
