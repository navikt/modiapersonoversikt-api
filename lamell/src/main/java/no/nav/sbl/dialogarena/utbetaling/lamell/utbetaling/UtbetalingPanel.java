package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning.DetaljPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class UtbetalingPanel extends Panel {

    public UtbetalingPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);

        add(
                createSkrivUtLink("skriv-ut"),
                new DetaljPanel("detaljpanel", utbetalingVM),
                new Label("statusBeskrivelse", utbetalingVM.getStatus()),
                new Label("kortUtbetalingsDato", utbetalingVM.getKortUtbetalingsDato()),
                new Label("beskrivelse", utbetalingVM.getBeskrivelse()),
                new Label("periodeMedKortDato", utbetalingVM.getPeriodeMedKortDato()),
                new Label("bruttoBelopMedValuta", utbetalingVM.getBruttoBelopMedValuta()),
                new Label("trekkMedValuta", utbetalingVM.getTrekkMedValuta()),
                new Label("belopMedValuta", utbetalingVM.getBelopMedValuta())
        );
        add(createClickBehavior());
    }

    private AjaxEventBehavior createClickBehavior() {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                target.appendJavaScript("$('#" + getMarkupId() + " .detaljpanel').animate({height: 'toggle'}, 300);");
            }
        };
    }

    private AjaxLink<Void> createSkrivUtLink(String id) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                String utbetalingslinje = "$('#" + getMarkupId() + "').closest('.utbetalingslinje')";
                String javascript = utbetalingslinje + ".children('.detaljpanel').show();" +
                                "$('body > .print .content').html(" + "'<div class=\"kolonne-hoyre\">'+" + utbetalingslinje + ".html()" + "+'</div>'" + ");" +
                                "window.print();";
                target.appendJavaScript(javascript);
            }
        };
    }

}
