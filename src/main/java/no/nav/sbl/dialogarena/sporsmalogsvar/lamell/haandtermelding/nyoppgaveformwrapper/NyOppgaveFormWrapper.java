package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.EnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.GsakKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.wicket.MarkupContainer;
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
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.isEmptyList;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.model.ModelUtils.nullValue;

public class NyOppgaveFormWrapper extends Panel {

    public static final String PRIORITET_NORMAL = "NORM";

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private EnhetService enhetService;

    private final InnboksVM innboksVM;
    private final List<AnsattEnhet> enheter;
    private final IChoiceRenderer<GsakKodeTema> gsakKodeChoiceRenderer;
    private final Form<NyOppgave> form;

    public NyOppgaveFormWrapper(String id, final InnboksVM innboksVM) {
        super(id);

        this.innboksVM = innboksVM;
        this.enheter = unmodifiableList(enhetService.hentAlleEnheter());
        this.gsakKodeChoiceRenderer = new ChoiceRenderer<>("tekst", "kode");
        this.form = new Form<>("nyoppgaveform", new CompoundPropertyModel<>(new NyOppgave()));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);

        form.setOutputMarkupId(true);
        form.add(
                lagTemaVelger(),
                lagOppgavetypeVelger(),
                lagEnhetVelger(),
                lagPrioritetVelger(),
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

        add(form);
    }

    public final void nullstillSkjema() {
        form.setModelObject(new NyOppgave());
    }

    protected void etterSubmit(AjaxRequestTarget target) {
    }

    private MarkupContainer lagTemaVelger() {
        IModel<List<GsakKodeTema.Tema>> temaModel = new AbstractReadOnlyModel<List<GsakKodeTema.Tema>>() {
            @Override
            public List<GsakKodeTema.Tema> getObject() {
                List<GsakKodeTema.Tema> gyldigeTema = new ArrayList<>();
                for (GsakKodeTema.Tema tema : gsakKodeverk.hentTemaListe()) {
                    if (!tema.oppgaveTyper.isEmpty()) {
                        gyldigeTema.add(tema);
                    }
                }
                return gyldigeTema;
            }
        };

        DropDownChoice<GsakKodeTema.Tema> temaDropDown = new DropDownChoiceMedFjerningAvDefault<GsakKodeTema.Tema>("tema", temaModel, gsakKodeChoiceRenderer, form) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                hentForeslattEnhet(innboksVM);
            }
        };
        temaDropDown.setRequired(true);

        return temaDropDown;
    }

    private MarkupContainer lagOppgavetypeVelger() {
        IModel<List<GsakKodeTema.OppgaveType>> typeModel = new OppdaterbarListeModel<GsakKodeTema.OppgaveType>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.OppgaveType> oppdater(GsakKodeTema.Tema tema) {
                return tema.oppgaveTyper;
            }
        };

        DropDownChoice<GsakKodeTema.OppgaveType> typeDropdown = new DropDownChoiceMedFjerningAvDefault<GsakKodeTema.OppgaveType>("type", typeModel, gsakKodeChoiceRenderer, form) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                hentForeslattEnhet(innboksVM);
            }
        };
        typeDropdown.setRequired(true);

        WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        typeContainer.add(typeDropdown);
        typeContainer.add(visibleIf(not(isEmptyList(typeModel))));

        return typeContainer;
    }

    private MarkupContainer lagEnhetVelger() {
        IModel<List<AnsattEnhet>> enhetModel = new OppdaterbarListeModel<AnsattEnhet>(form.getModel()) {
            @Override
            protected List<AnsattEnhet> oppdater(GsakKodeTema.Tema tema) {
                return enheter;
            }
        };

        AnsattEnhetDropdown ansattEnhetDropdown = new AnsattEnhetDropdown("enhet", new PropertyModel<AnsattEnhet>(form.getModel(), "enhet"), enheter);
        ansattEnhetDropdown.setRequired(true);

        WebMarkupContainer enhetContainer = new WebMarkupContainer("enhetContainer");
        enhetContainer.add(ansattEnhetDropdown);
        IModel<Boolean> visEnhetsValg = both(not(isEmptyList(enhetModel)))
                .and(not(nullValue(new PropertyModel(form.getModelObject(), "tema"))))
                .and(not(nullValue(new PropertyModel(form.getModelObject(), "type"))));
        enhetContainer.add(visibleIf(visEnhetsValg));

        return enhetContainer;
    }

    private MarkupContainer lagPrioritetVelger() {
        IModel<List<GsakKodeTema.Prioritet>> priModel = new OppdaterbarListeModel<GsakKodeTema.Prioritet>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.Prioritet> oppdater(GsakKodeTema.Tema tema) {
                setDefaultPrioritetNormal(tema);
                return tema.prioriteter;
            }
        };

        DropDownChoice<GsakKodeTema.Prioritet> prioritetDropdown = new DropDownChoiceMedFjerningAvDefault<>("prioritet", priModel, gsakKodeChoiceRenderer, form);
        prioritetDropdown.setRequired(true);

        WebMarkupContainer prioritetContainer = new WebMarkupContainer("prioritetContainer");
        prioritetContainer.add(prioritetDropdown);
        IModel<Boolean> visPrioritetsValg = both(not(isEmptyList(priModel)))
                .and(not(nullValue(new PropertyModel(form.getModelObject(), "tema"))));
        prioritetContainer.add(visibleIf(visPrioritetsValg));

        return prioritetContainer;
    }

    private void setDefaultPrioritetNormal(GsakKodeTema.Tema tema) {
        for (GsakKodeTema.Prioritet prioritet : tema.prioriteter) {
            if (prioritet.kode.contains(PRIORITET_NORMAL)) {
                form.getModelObject().prioritet = prioritet;
            }
        }
    }

    private void hentForeslattEnhet(InnboksVM innboksVM) {
        NyOppgave nyOppgave = form.getModelObject();
        if (nyOppgave.tema == null || nyOppgave.type == null) {
            return;
        }
        Optional<AnsattEnhet> foreslattEnhet = gsakService.hentForeslattEnhet(innboksVM.getFnr(), nyOppgave.tema.kode, nyOppgave.type.kode);
        if (foreslattEnhet.isSome()) {
            nyOppgave.enhet = foreslattEnhet.get();
        }
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

    private static class DropDownChoiceMedFjerningAvDefault<T> extends DropDownChoice<T> {
        protected DropDownChoiceMedFjerningAvDefault(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer, final Form<NyOppgave> form) {
            super(id, choices, renderer);
            add(changeListener(form));
        }

        private AjaxFormComponentUpdatingBehavior changeListener(final Form<NyOppgave> form) {
            return new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    onchange(target);
                    target.add(form);
                }
            };
        }

        protected void onchange(AjaxRequestTarget target) {
        }
    }
}
