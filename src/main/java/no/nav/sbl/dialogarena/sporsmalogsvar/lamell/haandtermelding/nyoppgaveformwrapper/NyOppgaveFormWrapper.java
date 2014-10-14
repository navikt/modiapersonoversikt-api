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
import org.apache.wicket.model.Model;
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
    private final MarkupContainer typeVelger;
    private final MarkupContainer enhetVelger;
    private final MarkupContainer prioritetVelger;

    private final IModel<Boolean> oppgaveOpprettet = Model.of(false);

    public NyOppgaveFormWrapper(String id, final InnboksVM innboksVM) {
        super(id);

        this.innboksVM = innboksVM;
        this.enheter = unmodifiableList(enhetService.hentAlleEnheter());
        this.gsakKodeChoiceRenderer = new ChoiceRenderer<>("tekst", "kode");
        this.form = new Form<>("nyoppgaveform", new CompoundPropertyModel<>(new NyOppgave()));
        this.typeVelger = lagOppgavetypeVelger();
        this.enhetVelger = lagEnhetVelger();
        this.prioritetVelger = lagPrioritetVelger();

        final FeedbackPanel feedbackPanelSuccess = new FeedbackPanel("feedbackOppgavePanel");
        feedbackPanelSuccess.setOutputMarkupId(true);

        final FeedbackPanel feedbackPanelError = new FeedbackPanel("feedback");
        feedbackPanelError.setOutputMarkupId(true);

        form.setOutputMarkupId(true);
        form.add(visibleIf(not(oppgaveOpprettet)));
        form.add(
                lagTemaVelger(),
                typeVelger,
                enhetVelger,
                prioritetVelger,
                new TextArea<String>("beskrivelse").setRequired(true),
                feedbackPanelError);
        form.add(new AjaxButton("opprettoppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                NyOppgave nyOppgave = form.getModelObject();
                nyOppgave.henvendelseId = innboksVM.getValgtTraad().getEldsteMelding().melding.id;
                nyOppgave.brukerId = innboksVM.getFnr();

                gsakService.opprettGsakOppgave(nyOppgave);
                etterSubmit(target);
                nullstillSkjema();
                oppgaveOpprettet.setObject(true);
                feedbackPanelSuccess.success(getString("oppgave.opprettet.bekreftelse"));
                target.add(form, feedbackPanelSuccess);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanelError);
            }
        });

        add(form, feedbackPanelSuccess);
    }

    public final void nullstillSkjema() {
        oppgaveOpprettet.setObject(false);
        form.getModelObject().nullstill();
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

        DropDownChoice<GsakKodeTema.Tema> temaDropDown = new DropDownChoiceMedFjerningAvDefault<GsakKodeTema.Tema>("tema", temaModel, gsakKodeChoiceRenderer) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                hentForeslattEnhet(innboksVM);
                target.add(typeVelger, enhetVelger, prioritetVelger);
            }
        };
        temaDropDown.setRequired(true).setOutputMarkupPlaceholderTag(true);

        return temaDropDown;
    }

    private MarkupContainer lagOppgavetypeVelger() {
        IModel<List<GsakKodeTema.OppgaveType>> typeModel = new OppdaterbarListeModel<GsakKodeTema.OppgaveType>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.OppgaveType> oppdater(GsakKodeTema.Tema tema) {
                return tema.oppgaveTyper;
            }
        };

        DropDownChoice<GsakKodeTema.OppgaveType> typeDropdown = new DropDownChoiceMedFjerningAvDefault<GsakKodeTema.OppgaveType>("type", typeModel, gsakKodeChoiceRenderer) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                hentForeslattEnhet(innboksVM);
                target.add(enhetVelger);
            }
        };
        typeDropdown.setRequired(true);

        WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        typeContainer.setOutputMarkupPlaceholderTag(true);

        typeContainer.add(typeDropdown);
        typeContainer.add(visibleIf(not(isEmptyList(typeModel))));

        return typeContainer;
    }

    private MarkupContainer lagEnhetVelger() {
        final IModel<List<AnsattEnhet>> enhetModel = new PropertyModel<>(this, "enheter");

        AnsattEnhetDropdown ansattEnhetDropdown = new AnsattEnhetDropdown("enhet", new PropertyModel<AnsattEnhet>(form.getModel(), "enhet"), enheter);
        ansattEnhetDropdown.setRequired(true);

        WebMarkupContainer enhetContainer = new WebMarkupContainer("enhetContainer");
        enhetContainer.setOutputMarkupPlaceholderTag(true);

        enhetContainer.add(ansattEnhetDropdown);
        IModel<Boolean> visEnhetsValg = both(not(isEmptyList(enhetModel)))
                .and(not(nullValue(new PropertyModel(form.getModelObject(), "tema"))))
                .and(not(nullValue(new PropertyModel(form.getModelObject(), "type"))));
        enhetContainer.add(visibleIf(visEnhetsValg));

        return enhetContainer;
    }

    private MarkupContainer lagPrioritetVelger() {
        IModel<List<GsakKodeTema.Prioritet>> prioritetModel = new OppdaterbarListeModel<GsakKodeTema.Prioritet>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.Prioritet> oppdater(GsakKodeTema.Tema tema) {
                for (GsakKodeTema.Prioritet prioritet : tema.prioriteter) {
                    if (prioritet.kode.startsWith(PRIORITET_NORMAL)) {
                        form.getModelObject().prioritet = prioritet;
                    }
                }
                return tema.prioriteter;
            }
        };

        DropDownChoice<GsakKodeTema.Prioritet> prioritetDropdown = new DropDownChoice<>("prioritet", prioritetModel, gsakKodeChoiceRenderer);
        prioritetDropdown.setRequired(true);

        WebMarkupContainer prioritetContainer = new WebMarkupContainer("prioritetContainer");
        prioritetContainer.setOutputMarkupPlaceholderTag(true);

        prioritetContainer.add(prioritetDropdown);
        IModel<Boolean> visPrioritetsValg = both(not(isEmptyList(prioritetModel))).and(not(nullValue(new PropertyModel(form.getModelObject(), "tema"))));
        prioritetContainer.add(visibleIf(visPrioritetsValg));

        return prioritetContainer;
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

    private abstract static class DropDownChoiceMedFjerningAvDefault<T> extends DropDownChoice<T> {
        protected DropDownChoiceMedFjerningAvDefault(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
            super(id, choices, renderer);
            add(changeListener());
        }

        private AjaxFormComponentUpdatingBehavior changeListener() {
            return new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    onchange(target);
                    target.add(DropDownChoiceMedFjerningAvDefault.this);
                }
            };
        }

        protected abstract void onchange(AjaxRequestTarget target);
    }
}
