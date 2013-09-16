package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import java.util.List;

public class VelgTemaPanel extends Panel {

    private final Temavelgerdelegat delegat;

    public VelgTemaPanel(String id, List<String> temaer, final Temavelgerdelegat delegat) {
        super(id);
        this.delegat = delegat;
        add(new Temaliste("temaliste", temaer));
    }

    class Temaliste extends PropertyListView<String> {

        public Temaliste(String id, List<? extends String> list) {
            super(id, list);
        }

        @Override
        protected void populateItem(final ListItem<String> item) {
            item.add(new Label("tema", new StringResourceModel(item.getModelObject(), this, null)));
            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    VelgTemaPanel.this.delegat.valgteTema(item.getModelObject(), target);
                }
            });
        }
    }

}
