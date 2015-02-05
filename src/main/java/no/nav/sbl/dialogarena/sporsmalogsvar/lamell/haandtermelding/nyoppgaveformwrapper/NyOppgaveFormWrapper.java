package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper;

import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.EnhetService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.GsakKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
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

    private final InnboksVM innboksVM;
    private final List<AnsattEnhet> enheter, foreslatteEnheter;
    private final IChoiceRenderer<GsakKodeTema> gsakKodeChoiceRenderer;
    private final Form<NyOppgave> form;
    private final MarkupContainer typeVelger, enhetVelger, prioritetVelger, underkategoriVelger;

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
        this.prioritetVelger = lagPrioritetVelger();
        this.underkategoriVelger = lagUnderkategoriVelger();

        final WebMarkupContainer feedbackPanelSuccess = new WebMarkupContainer("feedbackOppgavePanel");
        feedbackPanelSuccess.setOutputMarkupPlaceholderTag(true);
        feedbackPanelSuccess.add(visibleIf(oppgaveOpprettet));

        final FeedbackPanel feedbackPanelError = new FeedbackPanel("feedback");
        feedbackPanelError.setOutputMarkupId(true);

        form.setOutputMarkupId(true);
        form.add(visibleIf(not(oppgaveOpprettet)));
        form.add(
                lagTemaVelger(),
                typeVelger,
                enhetVelger,
                prioritetVelger,
                underkategoriVelger,
                new TextArea<String>("beskrivelse").setRequired(true),
                feedbackPanelError);
        form.add(new IndicatingAjaxButtonWithImageUrl("opprettoppgave", "../img/ajaxloader/svart/loader_svart_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                NyOppgave nyOppgave = form.getModelObject();
                nyOppgave.henvendelseId = innboksVM.getValgtTraad().getEldsteMelding().melding.id;
                nyOppgave.brukerId = innboksVM.getFnr();

                gsakService.opprettGsakOppgave(nyOppgave);
                etterSubmit(target);
                nullstillSkjema();
                oppgaveOpprettet.setObject(true);
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

        DropDownChoice<GsakKodeTema.Tema> temaDropDown = new DropDownChoiceMedFjerningAvDefault<GsakKodeTema.Tema>("tema", temaModel, gsakKodeChoiceRenderer) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                form.getModelObject().type = null;
                form.getModelObject().enhet = null;
                form.getModelObject().prioritet = null;

                target.add(typeVelger, enhetVelger, prioritetVelger, underkategoriVelger);
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
                target.add(enhetVelger);
            }
        });

        WebMarkupContainer underkategoriContainer = new WebMarkupContainer("underkategoriContainer");
        underkategoriContainer.setOutputMarkupPlaceholderTag(true);
        underkategoriContainer.add(new AttributeAppender("for", underkategoriDropdown.getMarkupId()));
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

        DropDownChoice<GsakKodeTema.OppgaveType> typeDropdown = new DropDownChoiceMedFjerningAvDefault<GsakKodeTema.OppgaveType>("type", typeModel, gsakKodeChoiceRenderer) {
            @Override
            protected void onchange(AjaxRequestTarget target) {
                hentForeslatteEnheter();
                target.add(enhetVelger);
            }
        };
        typeDropdown.setRequired(true);

        WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        typeContainer.setOutputMarkupPlaceholderTag(true);
        typeContainer.add(new AttributeAppender("for", typeDropdown.getMarkupId()));

        typeContainer.add(typeDropdown);
        typeContainer.add(visibleIf(not(isEmptyList(typeModel))));

        return typeContainer;
    }

    private MarkupContainer lagEnhetVelger() {
        final IModel<List<AnsattEnhet>> enhetModel = new PropertyModel<>(this, "enheter");

        AnsattEnhetDropdown ansattEnhetDropdown = new AnsattEnhetDropdown("enhet", new PropertyModel<AnsattEnhet>(form.getModel(), "enhet"), enheter, foreslatteEnheter);
        ansattEnhetDropdown.setRequired(true);

        WebMarkupContainer enhetContainer = new WebMarkupContainer("enhetContainer");
        enhetContainer.setOutputMarkupPlaceholderTag(true);

        enhetContainer.add(ansattEnhetDropdown);
        IModel<Boolean> visEnhetsValg = both(not(isEmptyList(enhetModel)))
                .and(not(nullValue(new PropertyModel<GsakKodeTema.Tema>(form.getModel(), "tema"))))
                .and(not(nullValue(new PropertyModel<GsakKodeTema.OppgaveType>(form.getModel(), "type"))));
        enhetContainer.add(visibleIf(visEnhetsValg));
        enhetContainer.add(new AttributeAppender("for", ansattEnhetDropdown.getMarkupId()));

        return enhetContainer;
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
        prioritetContainer.add(new AttributeAppender("for", prioritetDropdown.getMarkupId()));

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

    private static final Predicate<AnsattEnhet> GYLDIG_ENHET = new Predicate<AnsattEnhet>() {
        @Override
        public boolean evaluate(AnsattEnhet ansattEnhet) {
            return Integer.valueOf(ansattEnhet.enhetId) >= 100 && !ansattEnhet.enhetNavn.toLowerCase().contains("avviklet");
        }
    };
}
