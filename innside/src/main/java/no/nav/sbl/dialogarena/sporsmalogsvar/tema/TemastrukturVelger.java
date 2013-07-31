package no.nav.sbl.dialogarena.sporsmalogsvar.tema;

import java.util.List;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class TemastrukturVelger extends Panel {

    public TemastrukturVelger(String id, List<Temastruktur> temastrukturListe) {
        super(id);
        add(new ListView<Temastruktur>("temastruktur-liste", temastrukturListe) {

            @Override
            protected void populateItem(ListItem<Temastruktur> item) {
                Label temastruktur = new Label("temastruktur", item.getModelObject().navn);
                temastruktur.add(new AttributeAppender("class", "temastruktur"));
                item.add(temastruktur, new ListView<String>("tema-liste", item.getModelObject().temaliste) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        Label tema = new Label("tema", item.getModelObject());
                        tema.add(new AttributeAppender("class", "tema"));
                        item.add(tema);
                    }
                });
            }

        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(TemastrukturVelger.class, "tema.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(TemastrukturVelger.class, "tema.js")));
        response.render(OnDomReadyHeaderItem.forScript("temavelger();"));
    }

}