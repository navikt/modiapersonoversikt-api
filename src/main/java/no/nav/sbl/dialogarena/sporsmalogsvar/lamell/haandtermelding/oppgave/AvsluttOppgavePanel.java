package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static java.lang.String.format;

public class AvsluttOppgavePanel extends Panel {

    @Inject
    private GsakService gsakService;

    private final TextArea<String> beskrivelseFelt;

    public AvsluttOppgavePanel(String id, final Optional<String> oppgaveId) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        beskrivelseFelt = new TextArea<>("beskrivelse", new Model<String>());
        add(new Form("form")
                .add(beskrivelseFelt)
                .add(new AjaxButton("avsluttoppgave") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        gsakService.ferdigstillGsakOppgave(oppgaveId, beskrivelseFelt.getModelObject());
                    }
                }));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(format("$('#%s').val('%s');", beskrivelseFelt.getMarkupId(), getString("avsluttoppgave.standardbeskrivelse"))));
    }
}
