package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_LENKE_VALGT;

public class KvitteringsPanel extends Panel {

    private final AjaxLink startNyDialogLenke;

    private String kvitteringsmelding;
    private Temagruppe valgtTemagruppe;
    private Component[] komponenter = {};

    public KvitteringsPanel(String id) {
        super(id);
        setVisibilityAllowed(false);
        setOutputMarkupPlaceholderTag(true);

        Label temagruppemeldingLabel = new Label("temagruppemelding", new StringResourceModel("nydialogpanel.kvittering.andresocialetjenester", getDefaultModel()));
        temagruppemeldingLabel.add(visibleIf(temagruppeErAnsos()));

        Label kvitteringsmeldingLabel = new Label("kvitteringsmelding", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return KvitteringsPanel.this.kvitteringsmelding;
            }
        });

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
        add(startNyDialogLenke, temagruppemeldingLabel, kvitteringsmeldingLabel);
    }

    private AbstractReadOnlyModel<Boolean> temagruppeErAnsos() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return ANSOS.equals(valgtTemagruppe);
            }
        };
    }

    public void visKvittering(AjaxRequestTarget target, String kvitteringsmelding, final Component... komponenter) {
        visKvitteringsside(target, kvitteringsmelding, komponenter);
    }

    public void visTemagruppebasertKvittering(AjaxRequestTarget target, String kvitteringsmelding, Temagruppe valgtTemagruppe, final Component... komponenter) {
        this.valgtTemagruppe = valgtTemagruppe;
        visKvitteringsside(target, kvitteringsmelding, komponenter);
    }

    private void visKvitteringsside(AjaxRequestTarget target, String kvitteringsmelding, final Component... komponenter) {
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
