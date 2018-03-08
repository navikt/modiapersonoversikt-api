package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.meldinger;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

public class MeldingEtiketter extends ListView {

    private final String etikettMarkupId;

    public MeldingEtiketter(String id, String etikkettMarkupId, List<Etikett> etiketter) {
        super(id, etiketter);
        this.etikettMarkupId = etikkettMarkupId;
    }

    @Override
    protected void populateItem(ListItem item) {
        Etikett etikett = (Etikett) item.getModelObject();
        Label label = new Label(etikettMarkupId, etikett.getTekst());
        label.add(new AttributeAppender("class", etikett.getCssKlasse(), " "));
        item.add(label);
    }
}
