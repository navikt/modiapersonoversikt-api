package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import com.codahale.metrics.Timer;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.modia.metrics.MetricsFactory;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Ansatt;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.EnhetService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;

public class NyOppgaveFormWrapper extends Panel {

    public static final String PRIORITET_NORMAL = "NORM";

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private EnhetService enhetService;
    @Inject
    private AnsattService ansattService;

    private final InnboksVM innboksVM;
    private final List<AnsattEnhet> enheter, foreslatteEnheter;
    private final IChoiceRenderer<GsakKodeTema> gsakKodeChoiceRenderer;
    private final Form<NyOppgave> form;
    private final MarkupContainer typeVelger, enhetVelger, ansattVelger, prioritetVelger, underkategoriVelger;

    public final IModel<Boolean> oppgaveOpprettet = Model.of(false);

    public NyOppgaveFormWrapper(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        this.innboksVM = innboksVM;
        this.enheter = on(enhetService.hentAlleEnheter()).filter(GYLDIG_ENHET).collect();
        this.foreslatteEnheter = new ArrayList<>();
        this.gsakKodeChoiceRenderer = new ChoiceRenderer<>("tekst", "kode");
        this.form = new Form<>("nyoppgaveform", new CompoundPropertyModel<>(new NyOppgave()));
        this.typeVelger = lagOppgavetypeVelger();
        this.enhetVelger = lagEnhetVelger();
        this.ansattVelger = lagAnsattVelger();
        this.prioritetVelger = lagPrioritetVelger();
        this.underkategoriVelger = lagUnderkategoriVelger();
        MarkupContainer temaVelger = lagTemaVelger();
        TextArea<String> beskrivelse = new TextArea<>("beskrivelse");
        beskrivelse.setOutputMarkupId(true);

        final WebMarkupContainer feedbackPanelSuccess = new WebMarkupContainer("feedbackOppgavePanel");
        feedbackPanelSuccess.setOutputMarkupPlaceholderTag(true);
        feedbackPanelSuccess.add(visibleIf(oppgaveOpprettet));

        final FeedbackPanel feedbackPanelError = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanelError.setOutputMarkupId(true);

        form.setOutputMarkupId(true);
        form.add(visibleIf(not(oppgaveOpprettet)));
        form.add(
                temaVelger,
                typeVelger,
                enhetVelger,
                ansattVelger,
                prioritetVelger,
                underkategoriVelger,
                beskrivelse.setRequired(true),
                feedbackPanelError,
                FeedbackLabel.create(temaVelger),
                FeedbackLabel.create(typeVelger),
                FeedbackLabel.create(enhetVelger),
                FeedbackLabel.create(prioritetVelger),
                FeedbackLabel.create(beskrivelse)
        );


        form.add(new IndicatingAjaxButtonWithImageUrl("opprettoppgave", "../img/ajaxloader/svart/loader_svart_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                Timer.Context timer = MetricsFactory.createTimer("hendelse.opprettoppgave.time").time();
                try {
                    NyOppgave nyOppgave = form.getModelObject();
                    nyOppgave.henvendelseId = innboksVM.getValgtTraad().getEldsteMelding().melding.id;
                    nyOppgave.brukerId = innboksVM.getFnr();

                    gsakService.opprettGsakOppgave(nyOppgave);
                    etterSubmit(target);
                    nullstillSkjema();
                    oppgaveOpprettet.setObject(true);
                    target.add(form, feedbackPanelSuccess);
                } finally {
                    timer.stop();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanelError);
                FeedbackLabel.addFormLabelsToTarget(target, form);
            }
        });

        add(form, feedbackPanelSuccess);
    }

    public final void nullstillSkjema() {
        oppgaveOpprettet.setObject(false);
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

        DropDownChoice<GsakKodeTema.Tema> temaDropDown = new AjaxDropDownChoice<GsakKodeTema.Tema>("tema", temaModel, gsakKodeChoiceRenderer) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                form.getModelObject().type = null;
                form.getModelObject().enhet = null;
                form.getModelObject().prioritet = null;
                form.getModelObject().underkategori = null;

                target.add(typeVelger, enhetVelger, ansattVelger, prioritetVelger, underkategoriVelger);
                FeedbackLabel.addFormLabelsToTarget(target, form);
            }

            @Override
            protected void onerror(AjaxRequestTarget target) {
                form.getModelObject().tema = null;
                onchange(target);
            }
        };
        temaDropDown.setRequired(true).setOutputMarkupPlaceholderTag(true);

        return temaDropDown;
    }

    private MarkupContainer lagUnderkategoriVelger() {
        IModel<List<GsakKodeTema.Underkategori>> underkategoriModel = new OppdaterbarListeModel<GsakKodeTema.Underkategori>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.Underkategori> oppdater(GsakKodeTema.Tema tema) {
                return tema.underkategorier;
            }
        };
        DropDownChoice<GsakKodeTema.Underkategori> underkategoriDropdown = new DropDownChoice<>("underkategori", underkategoriModel, gsakKodeChoiceRenderer);
        underkategoriDropdown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                hentForeslatteEnheter();
                oppdaterAnsatteListe();
                target.add(enhetVelger, ansattVelger);
            }
        });

        WebMarkupContainer underkategoriContainer = new WebMarkupContainer("underkategoriContainer");
        underkategoriContainer.setOutputMarkupPlaceholderTag(true);
        underkategoriContainer.add(underkategoriDropdown);

        IModel<Boolean> visUnderkategoriValg = both(
                not(isEmptyList(underkategoriModel)))
                .and(not(nullValue(new PropertyModel<GsakKodeTema.Tema>(form.getModel(), "tema")))
                );
        underkategoriContainer.add(visibleIf(visUnderkategoriValg));

        return underkategoriContainer;
    }

    private MarkupContainer lagOppgavetypeVelger() {
        IModel<List<GsakKodeTema.OppgaveType>> typeModel = new OppdaterbarListeModel<GsakKodeTema.OppgaveType>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.OppgaveType> oppdater(GsakKodeTema.Tema tema) {
                return tema.oppgaveTyper;
            }
        };
        final WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        DropDownChoice<GsakKodeTema.OppgaveType> typeDropdown = new AjaxDropDownChoice<GsakKodeTema.OppgaveType>("type", typeModel, gsakKodeChoiceRenderer) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                hentForeslatteEnheter();
                oppdaterAnsatteListe();
                target.add(enhetVelger, ansattVelger);
                FeedbackLabel.addFormLabelsToTarget(target, form);
            }

            @Override
            protected void onerror(AjaxRequestTarget target) {
                form.getModelObject().type = null;
                onchange(target);
            }
        };
        typeDropdown.setRequired(true);


        typeContainer.setOutputMarkupPlaceholderTag(true);

        typeContainer.add(typeDropdown);
        typeContainer.add(visibleIf(not(isEmptyList(typeModel))));

        return typeContainer;
    }

    private MarkupContainer lagEnhetVelger() {
        final IModel<List<AnsattEnhet>> enhetModel = new PropertyModel<>(this, "enheter");
        final EnhetDropdown enhetDropdown = new EnhetDropdown("enhet", new PropertyModel<AnsattEnhet>(form.getModel(), "enhet"), enheter, foreslatteEnheter);

        enhetDropdown.setRequired(true);
        enhetDropdown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                oppdaterAnsatteListe();
                target.add(ansattVelger);
                // Prøvde å få til dette med target.forcusComponent(ansattVelger), men siden det er brukt en magisk
                // jQuery dropdown her så skal ikke fokus settes på ansattVelger, men på et input-element som
                // genereres av jQuery. En bieffekt av at man setter fokus med koden under er at fokus flyttes
                // til ansatt-dropdownen både når man trykker enter og tab når enhet-dropdownen har fokus.
                target.appendJavaScript("$(\"#ansattContainer\").find(\"input[class='ui-autocomplete-input']\").focus()");
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                super.onError(target, e);
                form.getModelObject().enhet = null;
                onUpdate(target);
            }
        });

        WebMarkupContainer enhetContainer = new WebMarkupContainer("enhetContainer");
        enhetContainer.setOutputMarkupPlaceholderTag(true);

        enhetContainer.add(enhetDropdown);
        IModel<Boolean> visEnhetsValg = both(not(isEmptyList(enhetModel)))
                .and(not(nullValue(new PropertyModel<GsakKodeTema.Tema>(form.getModel(), "tema"))))
                .and(not(nullValue(new PropertyModel<GsakKodeTema.OppgaveType>(form.getModel(), "type"))));
        enhetContainer.add(visibleIf(visEnhetsValg));

        return enhetContainer;
    }

    private MarkupContainer lagAnsattVelger() {
        final String ansattContainerId = "ansattContainer";
        WebMarkupContainer container = new WebMarkupContainer(ansattContainerId);
        container.setOutputMarkupPlaceholderTag(true);
        container.setMarkupId(ansattContainerId);
        container.add(new AnsattDropdown("ansatte", new PropertyModel<Ansatt>(form.getModel(), "valgtAnsatt"), new PropertyModel<List<Ansatt>>(form.getModel(), "ansatteTilknyttetEnhet")));
        container.add(visibleIf(not(nullValue(new PropertyModel<List<AnsattEnhet>>(form.getModel(), "enhet")))));
        return container;
    }

    private MarkupContainer lagPrioritetVelger() {
        IModel<List<GsakKodeTema.Prioritet>> prioritetModel = new OppdaterbarListeModel<GsakKodeTema.Prioritet>(form.getModel()) {
            @Override
            protected List<GsakKodeTema.Prioritet> oppdater(GsakKodeTema.Tema tema) {
                settDefaultNormalPrioritetHvisMulig(tema);
                return tema.prioriteter;
            }

            private void settDefaultNormalPrioritetHvisMulig(GsakKodeTema.Tema tema) {
                for (GsakKodeTema.Prioritet prioritet : tema.prioriteter) {
                    if (prioritet.kode.startsWith(PRIORITET_NORMAL)) {
                        form.getModelObject().prioritet = prioritet;
                        return;
                    }
                }
            }
        };

        DropDownChoice<GsakKodeTema.Prioritet> prioritetDropdown = new DropDownChoice<>("prioritet", prioritetModel, gsakKodeChoiceRenderer);
        prioritetDropdown.setRequired(true);

        WebMarkupContainer prioritetContainer = new WebMarkupContainer("prioritetContainer");
        prioritetContainer.setOutputMarkupPlaceholderTag(true);

        prioritetContainer.add(prioritetDropdown);
        IModel<Boolean> visPrioritetsValg = both(
                not(isEmptyList(prioritetModel)))
                .and(not(nullValue(new PropertyModel<GsakKodeTema.Tema>(form.getModel(), "tema")))
                );
        prioritetContainer.add(visibleIf(visPrioritetsValg));

        return prioritetContainer;
    }

    private void hentForeslatteEnheter() {
        NyOppgave nyOppgave = form.getModelObject();
        if (nyOppgave.tema == null || nyOppgave.type == null) {
            return;
        }
        foreslatteEnheter.clear();
        foreslatteEnheter.addAll(gsakService.hentForeslatteEnheter(innboksVM.getFnr(), nyOppgave.tema.kode, nyOppgave.type.kode, optional(nyOppgave.underkategori)));
        if (foreslatteEnheter.size() == 1) {
            nyOppgave.enhet = foreslatteEnheter.get(0);
        } else {
            nyOppgave.enhet = null;
        }
    }

    private void oppdaterAnsatteListe() {
        NyOppgave nyOppgave = form.getModelObject();
        AnsattEnhet enhet = nyOppgave.enhet;
        if (enhet != null) {
            nyOppgave.ansatteTilknyttetEnhet = ansattService.ansatteForEnhet(enhet);
        }
        nyOppgave.valgtAnsatt = null;
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

    private abstract static class AjaxDropDownChoice<T> extends DropDownChoice<T> {
        protected AjaxDropDownChoice(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
            super(id, choices, renderer);
            add(changeListener());
        }

        private AjaxFormComponentUpdatingBehavior changeListener() {
            return new OnChangeAjaxBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    onchange(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, RuntimeException e) {
                    onerror(target);
                }
            };
        }

        protected abstract void onchange(AjaxRequestTarget target);

        protected void onerror(AjaxRequestTarget target) {

        }
    }

    private static final Predicate<AnsattEnhet> GYLDIG_ENHET = new Predicate<AnsattEnhet>() {
        @Override
        public boolean evaluate(AnsattEnhet ansattEnhet) {
            return Integer.valueOf(ansattEnhet.enhetId) >= 100 && !ansattEnhet.enhetNavn.toLowerCase().contains("avviklet");
        }
    };
}
