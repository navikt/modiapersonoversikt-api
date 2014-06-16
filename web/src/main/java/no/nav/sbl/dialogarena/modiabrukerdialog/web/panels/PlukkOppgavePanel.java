package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.Tema;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static java.util.Arrays.asList;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class PlukkOppgavePanel extends Panel {

    public PlukkOppgavePanel(String id) {
        super(id);

        final IModel<Tema> valgtTema = new Model<>();
        Form<Tema> form = new Form<>("plukk-oppgave-form", valgtTema);
        AjaxSubmitLink plukkOppgave = new AjaxSubmitLink("plukk-oppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
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

        form.add(plukkOppgave, radioGroup);

        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forReference(new JavaScriptResourceReference(PlukkOppgavePanel.class, "plukkoppgave.js")));
    }
}
