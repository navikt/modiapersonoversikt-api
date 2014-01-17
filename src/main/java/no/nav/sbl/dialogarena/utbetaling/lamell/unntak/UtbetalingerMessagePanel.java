package no.nav.sbl.dialogarena.utbetaling.lamell.unntak;


import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Panel for å vise generelle meldinger om utbetalinger, typisk brukt ved ingen utbetalinger eller feil
 */
public class UtbetalingerMessagePanel extends Panel {

    public UtbetalingerMessagePanel(String id, String messageKey, String cssClass) {
        super(id);
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.add(new AttributeAppender("class", cssClass));
        container.add(new Label("message", new StringResourceModel(messageKey, this, null)));
        add(container);
    }
}
