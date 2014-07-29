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
import org.joda.time.LocalDate;

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

        // TODO: Endre når kodeverk er på plass
        form.add(new DropDownChoice<>("tema", asList("BAR", "BID", "HJE", "GRA")).setRequired(true));
        form.add(new DropDownChoice<>("enhet", asList("2820")).setRequired(true));
        form.add(new DropDownChoice<>("type", asList("KONT_BRUK_GEN")).setRequired(true));
        form.add(new DropDownChoice<>("prioritet", asList("NORM_GEN")).setRequired(true));
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
                        .withOpprettetAvEnhetId(2820)   // TODO: Endre til å hente den faktiske enhetsid
                        .withOpprettOppgave(
                            new WSOpprettOppgave()
                                .withAktivFra(LocalDate.now())
                                .withAnsvarligEnhetId(nyOppgave.enhet)
                                .withBeskrivelse(nyOppgave.beskrivelse)
                                .withFagomradeKode(nyOppgave.tema)
                                .withOppgavetypeKode(nyOppgave.type)
                                .withPrioritetKode(nyOppgave.prioritet)
                                .withLest(false)
                        ));
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
        target.appendJavaScript("animasjonSkliToggling('.nyoppgave',700)");
        target.add(this);
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void lukkNyOppgavePanel(AjaxRequestTarget target) {
        target.prependJavaScript("animasjonSkliTogglingMedVent('.nyoppgave',700)");
        this.setVisibilityAllowed(false);
        target.add(this);
    }


}
