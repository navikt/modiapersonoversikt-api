package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import no.nav.metrics.Timer;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.AnimertPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.wicket.conditional.ConditionalUtils.enabledIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.FERDIGSTILT_UTEN_SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkVM.MerkType.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel.OPPGAVE_OPPRETTET;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre.KontorsperrePanel.OPPRETT_OPPGAVE_TOGGLET;

public class MerkePanel extends AnimertPanel {

    public static final String TRAAD_MERKET = "sos.merkepanel.merket";

    @Inject
    private HenvendelseBehandlingService henvendelseService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    private final InnboksVM innboksVM;
    private final KontorsperrePanel kontorsperrePanel;
    private final FeedbackPanel feedbackPanel;
    private final CompoundPropertyModel<MerkVM> merkVM;
    private final AjaxButton merkKnapp;

    public MerkePanel(String id, final InnboksVM innboksVM) {
        super(id, true);

        this.innboksVM = innboksVM;

        String enhet = henvendelseService.getEnhet(innboksVM.getFnr());

        merkVM = new CompoundPropertyModel<>(new MerkVM());
        Form<MerkVM> merkForm = new Form<>("merkForm", merkVM);

        final RadioGroup<MerkType> merkRadioGroup = new RadioGroup<>("merkType");
        merkRadioGroup.setRequired(true);

        feedbackPanel = new FeedbackPanel("feedbackMerkPanel", new ComponentFeedbackMessageFilter(merkRadioGroup));
        feedbackPanel.setOutputMarkupId(true);
        merkForm.add(feedbackPanel);

        PropertyModel<Boolean> valgtTraadErKontorsperret = new PropertyModel<>(innboksVM, "valgtTraad.erKontorsperret()");
        IModel<Boolean> erTemagruppeSosialeTjenester = new PropertyModel<>(innboksVM, "valgtTraad.erTemagruppeSosialeTjenester()");
        IModel<Boolean> erMeldingstypeSporsmal = new PropertyModel<>(innboksVM, "valgtTraad.erMeldingstypeSporsmal()");
        IModel<Boolean> erBehandlet = new PropertyModel<>(innboksVM, "valgtTraad.erBehandlet()");
        IModel<Boolean> eldsteMeldingErJournalfort = new PropertyModel<>(innboksVM, "valgtTraad.erJournalfort()");
        IModel<Boolean> erFeilsendt = new PropertyModel<>(innboksVM, "valgtTraad.erFeilsendt()");

        IModel<Boolean> skalViseStandardMerkValg = both(not(eldsteMeldingErJournalfort)).and(not(erFeilsendt)).and(erBehandlet).and(not(valgtTraadErKontorsperret));
        IModel<Boolean> skalViseFerdigstillUtenSvarValg = both(erMeldingstypeSporsmal).and(not(valgtTraadErKontorsperret)).and(not(erBehandlet));

        Radio<MerkType> feilsendtRadio = new Radio<>("feilsendtRadio", Model.of(FEILSENDT));
        feilsendtRadio.add(enabledIf(skalViseStandardMerkValg));

        Component bidragRadioValg = new WebMarkupContainer("bidragRadioValg")
                .add(new Radio<>("bidragRadio", Model.of(BIDRAG)))
                .add(enabledIf(both(not(erTemagruppeSosialeTjenester)).and(skalViseStandardMerkValg)));

        Component kontorsperretRadioValg = new WebMarkupContainer("kontorsperretRadioValg")
                .add(new Radio<>("kontorsperretRadio", Model.of(KONTORSPERRET)))
                .add(enabledIf(skalViseStandardMerkValg));

        Component avsluttRadio = new WebMarkupContainer("avsluttRadioValg")
                .add(new Radio<>("avsluttRadio", Model.of(AVSLUTT)))
                .add(enabledIf(skalViseFerdigstillUtenSvarValg));

        kontorsperrePanel = new KontorsperrePanel("kontorsperrePanel", innboksVM, enhet);
        kontorsperrePanel.add(visibleIf(new PropertyModel<>(merkVM, "erKontorsperret()")));

        merkKnapp = new MerkKnapp("merk");

        merkRadioGroup.add(feilsendtRadio, bidragRadioValg, kontorsperretRadioValg, avsluttRadio, kontorsperrePanel);
        merkRadioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kontorsperrePanel, merkKnapp);
                refreshFeedbackPanel(target);
            }
        });

        merkForm.add(merkRadioGroup, merkKnapp);

        add(merkForm);
        add(new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                lukkPanel(target);
            }
        });
    }

    @RunOnEvents({OPPRETT_OPPGAVE_TOGGLET, OPPGAVE_OPPRETTET})
    public final void refreshMerkKnapp(AjaxRequestTarget target) {
        target.add(merkKnapp);
    }

    @RunOnEvents(OPPGAVE_OPPRETTET)
    public final void refreshFeedbackPanel(AjaxRequestTarget target) {
        target.add(feedbackPanel);
    }

    @RunOnEvents(OPPGAVE_OPPRETTET)
    public final void focusMerkKnapp(AjaxRequestTarget target) {
        target.appendJavaScript("$('#" + merkKnapp.getMarkupId() + "').focus();");
    }

    @Override
    public final void lukkPanel(AjaxRequestTarget target) {
        super.lukkPanel(target);
        merkVM.setObject(new MerkVM());
        kontorsperrePanel.reset();
    }

    private class MerkKnapp extends IndicatingAjaxButtonWithImageUrl {

        public MerkKnapp(String id) {
            super(id, "../img/ajaxloader/svart/loader_svart_48.gif");
            add(visibleIf(
                    either(new PropertyModel<>(kontorsperrePanel, "kanMerkeSomKontorsperret()"))
                            .or(new PropertyModel<>(merkVM, "erFeilsendt()"))
                            .or(new PropertyModel<>(merkVM, "erMerketBidrag()"))
                            .or(new PropertyModel<>(merkVM, "erAvsluttet()"))));
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            Timer timer = createTimer("hendelse.merk." + merkVM.getObject().getMerkType() + ".time");
            timer.start();
            try {
                switch (merkVM.getObject().getMerkType()) {
                    case FEILSENDT:
                        haandterFeilsendt(target);
                        break;
                    case BIDRAG:
                        haandterBidrag(target);
                        break;
                    case KONTORSPERRET:
                        haandterKontorsperring(target, form);
                        break;
                    case AVSLUTT:
                        haandterAvsluttet(target);
                }
            } finally {
                timer.stop();
                timer.report();
            }
        }

        private void haandterFeilsendt(AjaxRequestTarget target) {
            henvendelseService.merkSomFeilsendt(innboksVM.getValgtTraad());
            send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
            lukkPanel(target);
        }

        private void haandterBidrag(AjaxRequestTarget target) {
            henvendelseService.merkSomBidrag(innboksVM.getValgtTraad());
            send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
            lukkPanel(target);
        }

        private void haandterKontorsperring(AjaxRequestTarget target, Form<?> form) {
            if (kontorsperrePanel.kanMerkeSomKontorsperret()) {
                henvendelseService.merkSomKontorsperret(innboksVM.getFnr(), innboksVM.getValgtTraad());
                send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
                lukkPanel(target);
            } else {
                onError(target, form);
            }
        }

        private void haandterAvsluttet(AjaxRequestTarget target) {
            henvendelseService.merkSomAvsluttet(innboksVM.getValgtTraad());
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(innboksVM.getValgtTraad().getEldsteMelding().melding.oppgaveId, none());

            send(getPage(), Broadcast.DEPTH, TRAAD_MERKET);
            send(getPage(), Broadcast.DEPTH, FERDIGSTILT_UTEN_SVAR);
            lukkPanel(target);
        }

        @Override
        protected final void onError(AjaxRequestTarget target, Form<?> form) {
            refreshFeedbackPanel(target);
        }
    }
}
