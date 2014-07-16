package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.nyoppgave;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class NyOppgavePanel extends Panel {

    @Inject
    private Oppgavebehandling oppgavebehandling;

    private final CompoundPropertyModel<NyOppgave> nyOppgaveModel;

    public NyOppgavePanel(String id, final IModel<InnboksVM> innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        add(new Label("temagruppe", new StringResourceModel("${temagruppe}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding.melding"))));

        nyOppgaveModel = new CompoundPropertyModel<>(new NyOppgave());
        Form<NyOppgave> form = new Form<>("nyoppgaveform", nyOppgaveModel);
        add(form);

        form.add(new DropDownChoice<>("temagruppe", asList("Arbeid", "Uføre")).setRequired(true));
        form.add(new DropDownChoice<>("enhet", asList("1111", "2222")).setRequired(true));
        form.add(new DropDownChoice<>("type", asList("Kontakt NAV", "Kontakt Bruker")).setRequired(true));
        form.add(new DropDownChoice<>("prioritet", asList("1", "2")).setRequired(true));
        form.add(new TextArea<String>("beskrivelse").setRequired(true));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new AjaxSubmitLink("opprettoppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                NyOppgave nyOppgave = (NyOppgave) form.getModelObject();
                oppgavebehandling.opprettOppgave(
                        new WSOpprettOppgaveRequest()
                                .withOpprettetAvEnhetId(4112)
                                .withOpprettOppgave(
                                        new WSOpprettOppgave()
                                                .withHenvendelseId(innboksVM.getObject().getValgtTraad().getEldsteMelding().melding.id)
                                                .withFagomradeKode(nyOppgave.temagruppe)
                                                .withAnsvarligEnhetId(nyOppgave.enhet)
                                                .withOppgavetypeKode(nyOppgave.type)
                                                .withPrioritetKode(nyOppgave.prioritet)
                                                .withBeskrivelse(nyOppgave.beskrivelse)));
                lukkNyOppgavePanel(target);
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
