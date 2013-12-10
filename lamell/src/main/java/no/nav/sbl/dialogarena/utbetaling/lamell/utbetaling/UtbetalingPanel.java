package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, Utbetaling utbetaling) {
        super(id, new CompoundPropertyModel<>(utbetaling));
        DetaljPanel detaljPanel = createDetaljPanel(utbetaling);

        add(
                detaljPanel,
                new Label("statusBeskrivelse"),
                new Label("kortUtbetalingsDato"),
                new Label("beskrivelse"),
                new Label("periodeMedKortDato"),
                new Label("bruttoBelop"),
                new Label("trekk"),
                new Label("belopMedValuta")
        );
        add(createClickBehavior(detaljPanel));
    }

    private DetaljPanel createDetaljPanel(Utbetaling utbetaling) {
        return (DetaljPanel) new DetaljPanel("detaljpanel", utbetaling)
                .setOutputMarkupPlaceholderTag(true)
                .setVisibilityAllowed(false);
    }

    private AjaxEventBehavior createClickBehavior(final WebMarkupContainer container) {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                container.setVisibilityAllowed(!container.isVisibleInHierarchy());
                target.add(container);
            }
        };
    }

}
