package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        DetaljPanel detaljPanel = createDetaljPanel(utbetalingVM);

        add(
                detaljPanel,
                new Label("statusBeskrivelse", utbetalingVM.utbetaling.statusBeskrivelse),
                new Label("kortUtbetalingsDato", utbetalingVM.getKortUtbetalingsDato()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                new Label("periodeMedKortDato", utbetalingVM.getPeriodeMedKortDato()),
                new Label("bruttoBelopMedValuta", utbetalingVM.getBruttoBelopMedValuta()),
                new Label("trekkMedValuta", utbetalingVM.getTrekkMedValuta()),
                new Label("belopMedValuta", utbetalingVM.getBelopMedValuta())
        );
        add(createClickBehavior(detaljPanel));
    }

    private DetaljPanel createDetaljPanel(UtbetalingVM utbetalingVM) {
        return (DetaljPanel) new DetaljPanel("detaljpanel", utbetalingVM)
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
