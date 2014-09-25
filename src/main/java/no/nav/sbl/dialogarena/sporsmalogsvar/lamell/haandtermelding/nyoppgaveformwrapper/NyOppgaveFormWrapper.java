package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeTema;
import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyList;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeTema.OppgaveType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeTema.Prioritet;

public class NyOppgaveFormWrapper extends Panel {

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private EnhetService enhetService;

    private final Form<NyOppgave> form;
    private final List<AnsattEnhet> enheter;

    public NyOppgaveFormWrapper(String id, final InnboksVM innboksVM) {
        super(id);

        enheter = unmodifiableList(enhetService.hentAlleEnheter());

        form = new Form<>("nyoppgaveform", new CompoundPropertyModel<>(new NyOppgave()));
        add(form.setOutputMarkupId(true));

        IModel<List<AnsattEnhet>> enhetModel = new OppdaterbarListeModel<AnsattEnhet>(form.getModel()) {
            @Override
            protected List<AnsattEnhet> oppdater(GsakKodeTema.Tema tema) {
                List<AnsattEnhet> ansattEnheter = new ArrayList<>();

                Optional<AnsattEnhet> foreslattEnhet = gsakService.hentForeslattEnhet(innboksVM.getFnr(), tema.kode);
                if (foreslattEnhet.isSome()) {
                    ansattEnheter.add(foreslattEnhet.get());
                }

                ansattEnheter.addAll(enheter);
                return ansattEnheter;
            }
        };
        IModel<List<OppgaveType>> typeModel = new OppdaterbarListeModel<OppgaveType>(form.getModel()) {
            @Override
            protected List<OppgaveType> oppdater(GsakKodeTema.Tema tema) {
                return tema.oppgaveTyper;
            }
        };
        IModel<List<Prioritet>> priModel = new OppdaterbarListeModel<Prioritet>(form.getModel()) {
            @Override
            protected List<Prioritet> oppdater(GsakKodeTema.Tema tema) {
                return tema.prioriteter;
            }
        };
        IChoiceRenderer<GsakKodeTema> gsakKodeChoiceRenderer = new ChoiceRenderer<>("tekst", "kode");
        IChoiceRenderer<AnsattEnhet> enhetChoiceRenderer = new IChoiceRenderer<AnsattEnhet>() {
            @Override
            public Object getDisplayValue(AnsattEnhet object) {
                return object.enhetId + " " + object.enhetNavn;
            }

            @Override
            public String getIdValue(AnsattEnhet object, int index) {
                return object.enhetId;
            }
        };

        DropDownChoice<GsakKodeTema.Tema> temaDropDown = new DropDownChoice<>("tema", gsakKodeverk.hentTemaListe(), gsakKodeChoiceRenderer);
        temaDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(form);
            }
        });

        WebMarkupContainer enhetContainer = new WebMarkupContainer("enhetContainer");
        enhetContainer.add(new DropDownChoice<>("enhet", enhetModel, enhetChoiceRenderer).setRequired(true));
        enhetContainer.add(visibleIf(not(isEmptyList(enhetModel))));

        WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        typeContainer.add(new DropDownChoice<>("type", typeModel, gsakKodeChoiceRenderer).setRequired(true));
        typeContainer.add(visibleIf(not(isEmptyList(typeModel))));

        WebMarkupContainer prioritetContainer = new WebMarkupContainer("prioritetContainer");
        prioritetContainer.add(new DropDownChoice<>("prioritet", priModel, gsakKodeChoiceRenderer).setRequired(true));
        prioritetContainer.add(visibleIf(not(isEmptyList(priModel))));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);

        form.add(
                temaDropDown.setRequired(true),
                enhetContainer,
                typeContainer,
                prioritetContainer,
                new TextArea<String>("beskrivelse").setRequired(true),
                feedbackPanel);

        form.add(new AjaxButton("opprettoppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                NyOppgave nyOppgave = form.getModelObject();
                nyOppgave.henvendelseId = innboksVM.getValgtTraad().getEldsteMelding().melding.id;
                nyOppgave.brukerId = innboksVM.getFnr();

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

    public final void nullstillSkjema() {
        form.setModelObject(new NyOppgave());
    }

    protected void etterSubmit(AjaxRequestTarget target) {
    }

    private abstract static class OppdaterbarListeModel<T> extends AbstractReadOnlyModel<List<T>> {

        private final IModel<NyOppgave> model;

        private OppdaterbarListeModel(IModel<NyOppgave> model) {
            this.model = model;
        }

        @Override
        public final List<T> getObject() {
            GsakKodeTema.Tema tema = model.getObject().tema;
            if (tema != null) {
                return oppdater(tema);
            }
            return emptyList();
        }

        protected abstract List<T> oppdater(GsakKodeTema.Tema tema);
    }
}
