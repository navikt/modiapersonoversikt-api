package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_LENKE_VALGT;

public class KvitteringsPanel extends Panel {

    private final AjaxLink startNyDialogLenke;

    private String kvitteringsmelding;
    private Component[] komponenter = {};

    public KvitteringsPanel(String id) {
        super(id);
        setVisibilityAllowed(false);
        setOutputMarkupPlaceholderTag(true);
        add(new Label("kvitteringsmelding", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return kvitteringsmelding;
            }
        }));
        startNyDialogLenke = new AjaxLink("startNyDialogLenke") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                KvitteringsPanel.this.setVisibilityAllowed(false);
                for (Component component : komponenter) {
                    component.setVisibilityAllowed(true);
                }
                target.add(KvitteringsPanel.this);
                target.add(komponenter);
                send(getPage(), Broadcast.BREADTH, new NamedEventPayload(NY_DIALOG_LENKE_VALGT));
            }
        };
        add(startNyDialogLenke);
    }

    public void visKvittering(AjaxRequestTarget target, String kvitteringsmelding, final Component... komponenter) {
        this.kvitteringsmelding = kvitteringsmelding;
        this.komponenter = komponenter;
        for (Component komponent : komponenter) {
            komponent.setVisibilityAllowed(false);
        }
        this.setVisibilityAllowed(true);
        target.add(this);
        target.add(komponenter);
        target.focusComponent(startNyDialogLenke);
    }
}
