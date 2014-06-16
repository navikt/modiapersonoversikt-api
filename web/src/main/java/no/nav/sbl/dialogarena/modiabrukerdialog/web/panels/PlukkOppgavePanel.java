package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.Tema;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static java.util.Arrays.asList;

public class PlukkOppgavePanel extends Panel {

    public PlukkOppgavePanel(String id) {
        super(id);

        final IModel<Tema> valgtTema = new Model<>();
        Form<Tema> form = new Form<>("plukk-oppgave-form", valgtTema);
        AjaxLink<Void> velgTemagruppe = new AjaxLink<Void>("velg-temagruppe") {
            @Override
            public void onClick(AjaxRequestTarget target) {

            }
        };
        AjaxSubmitLink plukkOppgave = new AjaxSubmitLink("plukk-oppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                System.out.println(valgtTema.getObject());
            }
        };
        RadioGroup radioGroup = new RadioGroup<>("radio-group", valgtTema);
        radioGroup.add(new ListView<Tema>("liste", asList(Tema.values())) {
            @Override
            protected void populateItem(ListItem<Tema> item) {
                item.add(new Radio<>("temavalg", item.getModel()));
                item.add(new Label("temanavn", getString(item.getModelObject().name())));
            }
        });

        form.add(velgTemagruppe, plukkOppgave, radioGroup);

        add(form);
    }
}
