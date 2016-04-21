package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FILTER_ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FILTER_FEILET;

public class FilterPanel extends Panel {

    private FeedbackPanel valideringsfeil;

    public FilterPanel(String id, final FilterParametere filterParametere) {
        super(id);
        valideringsfeil = new FeedbackPanel("feedbackpanel");

        add(
                valideringsfeil.setOutputMarkupId(true),
                new PeriodeForm("periodeFormWrapper", filterParametere),
                new FilterForm("filterFormWrapper", filterParametere)
        );
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_ENDRET)
    private void filterEndretEvent(AjaxRequestTarget target) {
        target.add(valideringsfeil);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FILTER_FEILET)
    private void filterFeiletEvent(AjaxRequestTarget target) {
        target.add(valideringsfeil);
        target.appendJavaScript("Utbetalinger.skjulSnurrepipp();");
    }
}
