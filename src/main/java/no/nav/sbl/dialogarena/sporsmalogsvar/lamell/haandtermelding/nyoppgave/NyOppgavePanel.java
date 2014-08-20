package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgave;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class NyOppgavePanel extends AnimertPanel {

    @Inject
    private GsakService gsakService;

    private final CompoundPropertyModel<NyOppgave> nyOppgaveModel;

    public NyOppgavePanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        add(new Label("temagruppe", new StringResourceModel("${temagruppe}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding.melding"))));

        nyOppgaveModel = new CompoundPropertyModel<>(new NyOppgave());
        Form<NyOppgave> form = new Form<>("nyoppgaveform", nyOppgaveModel);
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
                lukkPanel(target);
                nullstillSkjema();
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(new AjaxLink<Void>("avbrytlink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        });
    }

    private void nullstillSkjema() {
        nyOppgaveModel.setObject(new NyOppgave());
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    @Override
    public void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
    }

}
