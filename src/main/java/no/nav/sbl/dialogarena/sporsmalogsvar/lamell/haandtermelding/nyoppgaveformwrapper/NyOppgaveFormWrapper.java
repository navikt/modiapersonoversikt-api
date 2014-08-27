package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;

public class NyOppgaveFormWrapper extends Panel {

    @Inject
    private GsakService gsakService;

    public NyOppgaveFormWrapper(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(new NyOppgave()));

        Form form = new Form<>("nyoppgaveform", getDefaultModel());
        add(form);

        // TODO: Endre når kodeverk er på plass
        form.add(new DropDownChoice<>("tema", asList("BAR", "BID", "HJE", "GRA")).setRequired(true));
        form.add(new DropDownChoice<>("enhet", asList("2820")).setRequired(true));
        form.add(new DropDownChoice<>("type", asList("KONT_BRUK_GEN")).setRequired(true));
        form.add(new DropDownChoice<>("prioritet", asList("NORM_GEN")).setRequired(true));
        form.add(new TextArea<String>("beskrivelse").setRequired(true));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new AjaxButton("opprettoppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                NyOppgave nyOppgave = (NyOppgave) form.getModelObject();
                nyOppgave.henvendelseId = innboksVM.getValgtTraad().getEldsteMelding().melding.id;

                gsakService.opprettGsakOppgave(nyOppgave);
                etterSubmit(target);
                nullstillSkjema();
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });
    }

    protected void etterSubmit(AjaxRequestTarget target) {}

    private void nullstillSkjema() {
        setDefaultModelObject(new NyOppgave());
    }
}
