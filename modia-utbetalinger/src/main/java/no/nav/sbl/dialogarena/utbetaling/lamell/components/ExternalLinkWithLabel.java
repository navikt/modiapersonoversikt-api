package no.nav.sbl.dialogarena.utbetaling.lamell.components;

import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

public class ExternalLinkWithLabel extends ExternalLink implements ILabelProvider {

    private IModel label;

    public ExternalLinkWithLabel(String id, String href, IModel label) {
        super(id, href);
        this.label = label;
    }

    @Override
    public IModel getLabel() {
        return label;
    }
}
