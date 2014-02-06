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

    private final Label messageLabel;

    public UtbetalingerMessagePanel(String id, String messageKey, String cssClass) {
        super(id);
        WebMarkupContainer container = new WebMarkupContainer("container");
        container.add(new AttributeAppender("class", cssClass));
        messageLabel = new Label("messageLabel", new StringResourceModel(messageKey, this, null));
        container.add(messageLabel);
        add(container);
    }

    public void endreMessageKey(String messageKey) {
        messageLabel.setDefaultModel(new StringResourceModel(messageKey, this, null));
    }
}
