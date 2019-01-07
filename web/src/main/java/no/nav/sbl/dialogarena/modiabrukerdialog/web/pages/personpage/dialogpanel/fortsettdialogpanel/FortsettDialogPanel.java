package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.metrics.Timer;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.MeldingBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar.LeggTilbakeDelvisSvarPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar.SkrivestotteProps;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.SVAR_AVBRUTT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.KOMMUNALE_TJENESTER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.siste;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService.OppgaveErFerdigstilt;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning.ENHET;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning.SAKSBEHANDLER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class FortsettDialogPanel extends GenericPanel<HenvendelseVM> {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final GrunnInfo grunnInfo;
    private final String oppgaveId;
    private final Melding sporsmal;
    private final List<Melding> svar;
    private final WebMarkupContainer svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;
    private final LeggTilbakeDelvisSvarPanel leggTilbakeDelvisSvarPanel;
    private final KvitteringsPanel kvittering;
    private final AjaxLink<Void> leggTilbakeKnapp;
    private final String behandlingsId;

    private final ReactComponentPanel traadVisning;
    public static final String TRAADVISNING_REACT_MODULE = "TraadVisning";
    public static final String TRAADVISNING_WICKET_CONTAINER_ID = "reactTraadVisningContainer";

    public FortsettDialogPanel(String id, GrunnInfo grunnInfo, final List<Melding> traad, Oppgave oppgave) {
        super(id, new CompoundPropertyModel<>(new HenvendelseVM()));
        this.grunnInfo = grunnInfo;
        this.oppgaveId = oppgave.oppgaveId;
        this.sporsmal = traad.get(0);
        this.svar = new ArrayList<>(traad.subList(1, traad.size()));
        this.behandlingsId = oppgave.svarHenvendelseId;

        HenvendelseVM henvendelseVM = getModelObject();
        henvendelseVM.oppgaveTilknytning = erTilknyttetAnsatt(traad);
        henvendelseVM.kanKunBesvaresMedSkriftligSvar = sisteMeldingErDelsvar(traad);
        settOppModellMedDefaultKanalOgTemagruppe(henvendelseVM);

        setOutputMarkupId(true);
        svarContainer = new WebMarkupContainer("svarcontainer");
        leggTilbakePanel = new LeggTilbakePanel("leggtilbakepanel", sporsmal.temagruppe, sporsmal.gjeldendeTemagruppe, oppgaveId, sporsmal, behandlingsId);
        SkrivestotteProps skrivestotteProps = new SkrivestotteProps(grunnInfo, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        leggTilbakeDelvisSvarPanel = new LeggTilbakeDelvisSvarPanel(behandlingsId, traad, skrivestotteProps);
        traadVisning = new ReactComponentPanel(TRAADVISNING_WICKET_CONTAINER_ID, TRAADVISNING_REACT_MODULE, new TraadVisningProps(traad));
        kvittering = new KvitteringsPanel("kvittering");

        leggTilbakeKnapp = lagLeggTilbakeKnapp();
        AjaxLink<Void> leggTilbakeMedDelvisSvarKnap = lagLeggTilbakeMedDelvisSvarKnapp();

        svarContainer.setOutputMarkupId(true);
        svarContainer.add(new FortsettDialogForm("fortsettdialogform", grunnInfo, getModel()), leggTilbakeKnapp, leggTilbakeMedDelvisSvarKnap);

        add(traadVisning, svarContainer, leggTilbakePanel, leggTilbakeDelvisSvarPanel, kvittering);
    }

    private boolean sisteMeldingErDelsvar(List<Melding> traad) {
        return traad.stream()
                .sorted(Comparator.comparing(melding -> melding.ferdigstiltDato))
                .reduce((first, second) -> second)
                .filter(Melding::erDelvisSvar)
                .isPresent();
    }

    private AjaxLink<Void> lagLeggTilbakeKnapp() {
        AjaxLink<Void> leggTilbakeKnapp = new AjaxLink<Void>("leggtilbake") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (traadenKanLeggesTilbake()) {
                    traadVisning.setVisibilityAllowed(true);
                    animertVisningToggle(target, svarContainer);
                    animertVisningToggle(target, leggTilbakePanel);
                    leggTilbakePanel.add(AttributeModifier.replace("aria-expanded", "true"));
                    target.add(FortsettDialogPanel.this);
                    target.focusComponent(leggTilbakePanel.hentForsteFokusKomponent());
                } else {
                    send(getPage(), BREADTH, SVAR_AVBRUTT);
                    henvendelseUtsendingService.avbrytHenvendelse(behandlingsId);
                }
            }
        };

        if (traadenKanLeggesTilbake()) {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.avbryt.leggtilbake")));
            leggTilbakeKnapp.add(AttributeModifier.replace("aria-controls", leggTilbakePanel.getMarkupId()));
        } else {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.avbryt.avbryt")));
        }

        leggTilbakePanel.setVisibilityAllowed(false);

        return leggTilbakeKnapp;
    }

    private AjaxLink<Void> lagLeggTilbakeMedDelvisSvarKnapp() {
        AjaxLink<Void> leggTilbakeDelvisKnapp = new AjaxLink<Void>("leggtilbakemeddelvissvar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (kanBesvaresDelvis()) {
                    traadVisning.setVisibilityAllowed(false);
                    target.prependJavaScript(settTekstInnIDelvisSvarTekstfeltHack());
                    svarContainer.setVisibilityAllowed(false);
                    leggTilbakeDelvisSvarPanel.setVisibilityAllowed(true);
                    leggTilbakeDelvisSvarPanel.add(AttributeModifier.replace("aria-expanded", "true"));
                    target.add(FortsettDialogPanel.this);
                }
            }

            private String settTekstInnIDelvisSvarTekstfeltHack() {
                // Wicket kjenner ikke innhold i tekstfeltet før form`et submittes.
                // Alle forsøk på å hente teksten som er skrevet i onClick-funksjonen returnerer null
                // Mellomlagrer på window, dette hentes ut i delvis.svar.js før variablen settes til undefined.
                return "(function () {" +
                        "    const value = document.getElementsByClassName('dialogpanel')[0].getElementsByTagName('textarea')[0].value;" +
                        "    window.dialogTekst = value; " +
                        "})();";
            }
        };

        if (kanBesvaresDelvis()) {
            leggTilbakeDelvisKnapp.add(new Label("leggtilbakedelvistekst", new ResourceModel("fortsettdialogpanel.leggtilbakedelvis")));
            leggTilbakeDelvisKnapp.add(AttributeModifier.replace("aria-controls", leggTilbakeDelvisSvarPanel.getMarkupId()));
        } else {
            leggTilbakeDelvisKnapp.setVisibilityAllowed(false);
        }

        leggTilbakeDelvisSvarPanel.setVisibilityAllowed(false);

        return leggTilbakeDelvisKnapp;
    }
        
    private boolean kanBesvaresDelvis() {
        return sporsmal.erSporsmalSkriftlig() && erSporsmalUbesvart();
    }

    private boolean erSporsmalUbesvart() {
        return svar.stream().noneMatch(Melding::erSvarSkriftlig);
    }

    static HenvendelseVM.OppgaveTilknytning erTilknyttetAnsatt(List<Melding> traad) {
        boolean tilknyttetAnsatt;
        if (harUtgaaendeSporsmal(traad)) {
            tilknyttetAnsatt = siste(traad).get().erTilknyttetAnsatt;
        } else {
            tilknyttetAnsatt = true;
        }
        return tilknyttetAnsatt ? SAKSBEHANDLER : ENHET;
    }

    private static boolean harUtgaaendeSporsmal(List<Melding> traad) {
        return traad.stream().anyMatch(melding -> SPORSMAL_MODIA_UTGAAENDE.equals(melding.meldingstype));
    }

    private boolean traadenKanLeggesTilbake() {
        return sporsmalErUbesvart() && sporsmal.erSporsmalSkriftlig();
    }

    private boolean sporsmalErUbesvart() {
        return svar.stream()
                .noneMatch((melding) ->
                        melding.meldingstype == Meldingstype.SVAR_OPPMOTE ||
                                melding.meldingstype == Meldingstype.SVAR_SKRIFTLIG ||
                                melding.meldingstype == Meldingstype.SVAR_TELEFON);
    }

    private void settOppModellMedDefaultKanalOgTemagruppe(HenvendelseVM henvendelseVM) {
        henvendelseVM.kanal = TEKST;
        henvendelseVM.temagruppe = Temagruppe.valueOf(sporsmal.temagruppe);
        henvendelseVM.gjeldendeTemagruppe = sporsmal.gjeldendeTemagruppe;
        henvendelseVM.setTraadJournalfort(sporsmal.journalfortDato);
    }

    @RunOnEvents(LeggTilbakePanel.LEGG_TILBAKE_AVBRUTT)
    public void skjulLeggTilbakePanel(AjaxRequestTarget target) {
        animertVisningToggle(target, svarContainer);
        animertVisningToggle(target, leggTilbakePanel);
        target.add(this);
        target.focusComponent(leggTilbakeKnapp);
    }

    @RunOnEvents(LeggTilbakeDelvisSvarPanel.AVBRYT_CALLBACK_ID)
    public void skjulDelvisSvarPanel(AjaxRequestTarget target) {
        traadVisning.setVisibilityAllowed(true);
        svarContainer.setVisibilityAllowed(true);
        target.add(this);
    }

    private class FortsettDialogForm extends Form<HenvendelseVM> {

        private final FeedbackPanel feedbackPanel;
        private final AjaxButton sendKnapp;
        private transient Timer timer;

        public FortsettDialogForm(String id, final GrunnInfo grunnInfo, final IModel<HenvendelseVM> model) {
            super(id, model);
            timer = createTimer("hendelse.besvar");
            timer.start();
            final IModel<HenvendelseVM> henvendelseVM = getModel();

            add(new FortsettDialogFormElementer("fortsettdialogformelementer", grunnInfo, henvendelseVM));

            feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
            feedbackPanel.setOutputMarkupId(true);
            add(feedbackPanel);

            sendKnapp = new IndicatingAjaxButtonWithImageUrl("send", "../img/ajaxloader/graa/loader_graa_48.gif") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (!KOMMUNALE_TJENESTER.contains(henvendelseVM.getObject().gjeldendeTemagruppe)
                            && henvendelseVM.getObject().brukerKanSvareSkalEnables().getObject()
                            && henvendelseVM.getObject().brukerKanSvare
                            && henvendelseVM.getObject().valgtSak == null
                            && !henvendelseVM.getObject().traadJournalfort) {
                        error(getString("valgtSak.Required"));
                        onError(target, form);
                    } else {
                        sendOgVisKvittering(henvendelseVM.getObject(), target);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                    FeedbackLabel.addFormLabelsToTarget(target, form);
                }
            };
            sendKnapp.add(new AttributeModifier("value", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    return format(getString("fortsettdialogform.knapp.send"), grunnInfo.bruker.fornavn);
                }
            }));
            add(sendKnapp);
        }

        private void sendOgVisKvittering(HenvendelseVM henvendelseVM, AjaxRequestTarget target) {
            try {
                sendHenvendelse(henvendelseVM);
                send(getPage(), BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
                kvittering.visKvittering(target, getString(henvendelseVM.getKvitteringsTekstKeyBasertPaaBrukerKanSvare("fortsettdialogpanel")),
                        traadVisning, svarContainer, leggTilbakePanel);
            } catch (OppgaveErFerdigstilt oppgaveErFerdigstilt) {
                error(getString("fortsettdialogform.feilmelding.oppgaveferdigstilt"));
                sendKnapp.setVisibilityAllowed(false);
                leggTilbakeKnapp.setVisibilityAllowed(false);
                target.add(feedbackPanel, sendKnapp, leggTilbakeKnapp);
            } catch (JournalforingFeilet e) {
                send(getPage(), BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
                kvittering.visKvittering(target, getString("dialogpanel.feilmelding.journalforing"),
                        traadVisning, svarContainer, leggTilbakePanel);
            } catch (Exception e) {
                error(getString("dialogpanel.feilmelding.send.henvendelse"));
                target.add(feedbackPanel);
            } finally {
                if (timer != null) {
                    timer.stop();
                    timer.report();
                }
            }
        }

        private void sendHenvendelse(HenvendelseVM henvendelseVM) throws Exception {
            Meldingstype meldingstype = meldingstype(henvendelseVM.kanal, henvendelseVM.brukerKanSvare);
            Melding melding = new MeldingBuilder()
                    .withHenvendelseVM(henvendelseVM)
                    .withEldsteMeldingITraad(ofNullable(sporsmal))
                    .withMeldingstype(meldingstype)
                    .withFnr(grunnInfo.bruker.fnr)
                    .withNavident(getSubjectHandler().getUid())
                    .withValgtEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())
                    .build()
                    .withBrukersEnhet(sporsmal.brukersEnhet);

            Optional<Sak> sak = Optional.empty();
            if (melding.meldingstype.equals(SPORSMAL_MODIA_UTGAAENDE) && !henvendelseVM.traadJournalfort) {
                sak = ofNullable(henvendelseVM.valgtSak);
            }

            henvendelseUtsendingService.ferdigstillHenvendelse(melding, ofNullable(oppgaveId), sak, behandlingsId, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        }

        private Meldingstype meldingstype(Kanal kanal, boolean brukerKanSvare) {

            if (brukerKanSvare && kanal.equals(TEKST)) {
                return SPORSMAL_MODIA_UTGAAENDE;
            } else {
                switch (kanal) {
                    case TEKST:
                        return SVAR_SKRIFTLIG;
                    case OPPMOTE:
                        return SVAR_OPPMOTE;
                    case TELEFON:
                        return SVAR_TELEFON;
                }
            }

            throw new RuntimeException("Fant ikke passende meldingstype");
        }

        @Override
        protected void onRemove() {
            super.onRemove();
            if (timer != null) {
                timer.stop();
                timer.report();
            }
        }
    }

}
