package no.nav.sbl.dialogarena.utbetaling.lamell.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class PrintEkspanderContainer extends Panel {

    public PrintEkspanderContainer(String id, String markupId) {
        super(id);

        add(createSkrivUtLink(markupId));
    }

    private Component createSkrivUtLink(final String markupId) {
        return new AjaxLink<Void>("skriv-ut") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("Utbetalinger.skrivUt($('#" + markupId + "'));");
            }
        };
    }
}
