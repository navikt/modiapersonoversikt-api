package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;

public class NAVEnhetPanel extends Panel {

    public NAVEnhetPanel(String id) {
        super(id);

        final Form form = new Form<>("kontorform");
        form.setOutputMarkupPlaceholderTag(true);

//        form.add(new AjaxButton("avbryt") {
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
//                // Lukk panel
//            }
//        });

        java.util.List<Kontor> kontorer = Arrays.asList(new Kontor("Sagene"), new Kontor("Grunerløkka"), new Kontor("Schous Plass"));

        IModel<Kontor> valgtKontor = new Model<Kontor>();
        RadioGroup gruppe = new RadioGroup("kontor", valgtKontor);

        gruppe.add(new ListView<Kontor>("kontorvalg", kontorer) {
            protected void populateItem(ListItem<Kontor> item) {
                item.add(new Radio("kontorknapp", item.getModel()));
                item.add(new Label("kontornavn", item.getModelObject().navn));
            }
        });

        form.add(gruppe);


//        form.add(new AjaxButton("velg") {
//             @Override
//             protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
//             }
//         });

        add(form);

    }
}
