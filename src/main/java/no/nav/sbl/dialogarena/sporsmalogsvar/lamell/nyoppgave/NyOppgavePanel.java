package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.nyoppgave;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;

public class NyOppgavePanel extends Panel {

    @Inject
    private OppgavebehandlingV3 oppgavebehandling;

    private final CompoundPropertyModel<NyOppgave> nyOppgaveModel;

    public NyOppgavePanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        add(new Label("temagruppe", new StringResourceModel("${temagruppe}", this, new PropertyModel<>(innboksVM, "valgtTraad.eldsteMelding.melding"))));

        nyOppgaveModel = new CompoundPropertyModel<>(new NyOppgave());
        Form<NyOppgave> form = new Form<>("nyoppgaveform", nyOppgaveModel);
        add(form);

        form.add(new DropDownChoice<>("tema", asList("Arbeid", "Uføre")).setRequired(true));
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
                                                .withHenvendelseId(innboksVM.getValgtTraad().getEldsteMelding().melding.id)
                                                .withFagomradeKode(nyOppgave.tema)
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

    public void apneNyOppgavePanel(AjaxRequestTarget target) {
        this.setVisibilityAllowed(true);
        target.appendJavaScript("apneMedAnimasjon('.nyoppgave',400)");
        target.add(this);
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void lukkNyOppgavePanel(AjaxRequestTarget target) {
        if (isVisibleInHierarchy()) {
            target.prependJavaScript("oppgavePanelLukket|lukkMedAnimasjon('.nyoppgave',400,oppgavePanelLukket)");
            this.setVisibilityAllowed(false);
            target.add(this);
        }
    }


}
