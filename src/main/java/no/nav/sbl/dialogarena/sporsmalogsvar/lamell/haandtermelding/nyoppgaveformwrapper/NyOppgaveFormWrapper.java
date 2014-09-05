package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKode;
import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class NyOppgaveFormWrapper extends Panel {

    @Inject
    private GsakService gsakService;

    @Inject
    private GsakKodeverk gsakKodeverk;


    public NyOppgaveFormWrapper(String id, final InnboksVM innboksVM) {
        super(id, new CompoundPropertyModel<>(new NyOppgave()));

        final Form form = new Form<>("nyoppgaveform", getDefaultModel());
        add(form);


        IModel<List<GsakKode.OppgaveType>> typeModel = new AbstractReadOnlyModel<List<GsakKode.OppgaveType>>() {
            @Override
            public List<GsakKode.OppgaveType> getObject() {
                GsakKode.Tema tema = ((NyOppgave)form.getModelObject()).tema;
                if(tema != null) return tema.oppgaveTyper;
                return emptyList();
            }
        };
        IModel<List<GsakKode.Prioritet>> priModel = new AbstractReadOnlyModel<List<GsakKode.Prioritet>>() {
            @Override
            public List<GsakKode.Prioritet> getObject() {
                GsakKode.Tema tema = ((NyOppgave)form.getModelObject()).tema;
                if(tema != null) return tema.prioriteter;
                return emptyList();
            }
        };
        ChoiceRenderer<GsakKode> choiceRenderer = new ChoiceRenderer<>("tekst", "kode");
        final DropDownChoice<GsakKode.Tema> temaDropDown = new DropDownChoice<>("tema", gsakKodeverk.hentTemaListe(), choiceRenderer);
        final DropDownChoice<GsakKode.OppgaveType> typeDropDown = new DropDownChoice<>("type", typeModel, choiceRenderer);
        final DropDownChoice<GsakKode.Prioritet> prioritetDropDown = new DropDownChoice<>("prioritet", priModel, choiceRenderer);

        temaDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(typeDropDown);
                target.add(prioritetDropDown);
            }
        });
        form.add(temaDropDown.setRequired(true));
        form.add(new DropDownChoice<>("enhet", asList("2820")).setRequired(true));
        form.add(typeDropDown.setRequired(true).setOutputMarkupId(true));
        form.add(prioritetDropDown.setRequired(true).setOutputMarkupId(true));
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

    protected void etterSubmit(AjaxRequestTarget target) {
    }

    private void nullstillSkjema() {
        setDefaultModelObject(new NyOppgave());
    }

}
