package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.nyoppgave;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class NyOppgavePanel extends Panel {

    private final CompoundPropertyModel<NyOppgave> nyOppgaveModel;

    public NyOppgavePanel(String id, IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        add(new Label("temagruppe", new StringResourceModel("${temagruppe}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding.melding"))));

        nyOppgaveModel = new CompoundPropertyModel<>(new NyOppgave());
        Form<NyOppgave> form = new Form<>("nyoppgaveform", nyOppgaveModel);
        add(form);

        form.add(new TextArea<String>("beskrivelse"));

        form.add(new AjaxSubmitLink("opprettoppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                lukkNyOppgavePanel(target);
                nullstillSkjema();
            }
        });

        add(new AjaxLink<Void>("avbrytlink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkNyOppgavePanel(target);
            }
        });
    }

    private void nullstillSkjema() {
        nyOppgaveModel.setObject(new NyOppgave());
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    private void lukkNyOppgavePanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(false);
        target.add(this);
    }

}
