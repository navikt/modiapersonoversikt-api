package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class AvsluttOppgavePanel extends Panel {

    public AvsluttOppgavePanel(String id) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        Form form = new Form("form");

        form.add(new TextArea<>("beskrivelse", new Model<>()));
        form.add(new AjaxButton("avsluttoppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }
        });

        add(form);
    }
}
