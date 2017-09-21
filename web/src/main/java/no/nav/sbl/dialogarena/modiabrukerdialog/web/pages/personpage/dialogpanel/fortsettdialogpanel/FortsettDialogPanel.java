package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.metrics.Timer;
import no.nav.modig.modia.feedbackform.FeedbackLabel;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.MeldingBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar.LeggTilbakeDelvisSvarPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.*;

import javax.inject.Inject;
import java.util.*;

import static java.lang.String.format;
import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.SVAR_AVBRUTT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.KOMMUNALE_TJENESTER;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.siste;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService.OppgaveErFerdigstilt;
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
    private final Optional<String> oppgaveId;
    private final Melding sporsmal;
    private final List<Melding> svar;
    private final WebMarkupContainer traadContainer, svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;
    private final LeggTilbakeDelvisSvarPanel leggTilbakeDelvisSvarPanel;
    private final KvitteringsPanel kvittering;
    private final WebMarkupContainer visTraadContainer;
    private final AjaxLink<Void> leggTilbakeKnapp;
    private String behandlingsId;

    public FortsettDialogPanel(String id, GrunnInfo grunnInfo, final List<Melding> traad, Optional<String> oppgaveId) {
        super(id, new CompoundPropertyModel<>(new HenvendelseVM()));
        this.grunnInfo = grunnInfo;
        this.oppgaveId = oppgaveId;
        this.sporsmal = traad.get(0);
        this.svar = new ArrayList<>(traad.subList(1, traad.size()));
        getModelObject().oppgaveTilknytning = erTilknyttetAnsatt(traad);
        settOppModellMedDefaultKanalOgTemagruppe(getModelObject());
        setOutputMarkupId(true);
        opprettHenvendelse();

        visTraadContainer = new WebMarkupContainer("vistraadcontainer");
        traadContainer = new WebMarkupContainer("traadcontainer");
        svarContainer = new WebMarkupContainer("svarcontainer");
        leggTilbakePanel = new LeggTilbakePanel("leggtilbakepanel", sporsmal.temagruppe, sporsmal.gjeldendeTemagruppe, oppgaveId, sporsmal, behandlingsId);
        leggTilbakeDelvisSvarPanel = new LeggTilbakeDelvisSvarPanel();
        kvittering = new KvitteringsPanel("kvittering");

        visTraadContainer.setOutputMarkupPlaceholderTag(true);
        visTraadContainer.setVisibilityAllowed(!svar.isEmpty());
        visTraadContainer
                .add(new WebMarkupContainer("ekspanderingspil").add(hasCssClassIf("ekspandert", new PropertyModel<Boolean>(traadContainer, "visibilityAllowed"))))
                .add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        animertVisningToggle(target, traadContainer);
                        target.add(visTraadContainer, traadContainer);
                    }
                });

        traadContainer.setOutputMarkupPlaceholderTag(true);
        traadContainer.setVisibilityAllowed(svar.isEmpty());
        traadContainer.add(
                new TidligereMeldingPanel("sporsmal", "sporsmal", sporsmal.temagruppe, sporsmal.opprettetDato, sporsmal.fritekst, !svar.isEmpty()),
                new ListView<Melding>("svarliste", svar) {
                    @Override
                    protected void populateItem(ListItem<Melding> item) {
                        Melding melding = item.getModelObject();
                        String type = melding.meldingstype.name().substring(0, melding.meldingstype.name().indexOf("_")).toLowerCase();
                        item.add(new TidligereMeldingPanel("svar", type, melding.temagruppe, melding.opprettetDato, melding.fritekst, melding.navIdent, true));
                    }
                }
        );

        leggTilbakeKnapp = lagLeggTilbakeKnapp();
        AjaxLink<Void> leggTilbakeMedDelvisSvarKnap = lagLeggTilbakeMedDelvisSvarKnapp();

        svarContainer.setOutputMarkupId(true);
        svarContainer.add(new FortsettDialogForm("fortsettdialogform", grunnInfo, getModel()), leggTilbakeKnapp, leggTilbakeMedDelvisSvarKnap);

        add(visTraadContainer, traadContainer, svarContainer, leggTilbakePanel, leggTilbakeDelvisSvarPanel, kvittering);
    }

    private AjaxLink<Void> lagLeggTilbakeKnapp() {
        AjaxLink<Void> leggTilbakeKnapp = new AjaxLink<Void>("leggtilbake") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (traadenErEtEnkeltSporsmalFraBruker()) {
                    traadContainer.setVisibilityAllowed(true);
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

        if (traadenErEtEnkeltSporsmalFraBruker()) {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.avbryt.leggtilbake")));
            leggTilbakeKnapp.add(AttributeModifier.replace("aria-controls", leggTilbakePanel.getMarkupId()));
        } else {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.avbryt.avbryt")));
        }

        leggTilbakePanel.setVisibilityAllowed(false);

        return leggTilbakeKnapp;
    }

    private AjaxLink<Void> lagLeggTilbakeMedDelvisSvarKnapp() {
        AjaxLink<Void> leggTilbakeKnapp = new AjaxLink<Void>("leggtilbakemeddelvissvar") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (traadenErEtEnkeltSporsmalFraBruker()) {
                    traadContainer.setVisibilityAllowed(false);
                    animertVisningToggle(target, svarContainer);
                    animertVisningToggle(target, leggTilbakeDelvisSvarPanel);
                    leggTilbakeDelvisSvarPanel.add(AttributeModifier.replace("aria-expanded", "true"));
                    target.add(FortsettDialogPanel.this);
                }
            }
        };

        if (traadenErEtEnkeltSporsmalFraBruker()) {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.leggtilbakedelvis")));
            leggTilbakeKnapp.add(AttributeModifier.replace("aria-controls", leggTilbakeDelvisSvarPanel.getMarkupId()));
        }

        leggTilbakeDelvisSvarPanel.setVisibilityAllowed(false);

        return leggTilbakeKnapp;
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
        return on(traad).exists(where(Melding.TYPE, equalTo(SPORSMAL_MODIA_UTGAAENDE)));
    }

    private static boolean ingenAvType(List<Melding> svar, Meldingstype type) {
        return !on(svar).exists(where(Melding.TYPE, equalTo(type)));
    }

    private boolean traadenErEtEnkeltSporsmalFraBruker() {
        return svar.isEmpty() && sporsmal.meldingstype.equals(SPORSMAL_SKRIFTLIG);
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

    private void opprettHenvendelse() {
        String type = SVAR_SKRIFTLIG.toString();
        String fnr = grunnInfo.bruker.fnr;
        String behandlingskjedeId = sporsmal.traadId;

        behandlingsId = henvendelseUtsendingService.opprettHenvendelse(type, fnr, behandlingskjedeId);
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
                        visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
            } catch (OppgaveErFerdigstilt oppgaveErFerdigstilt) {
                error(getString("fortsettdialogform.feilmelding.oppgaveferdigstilt"));
                sendKnapp.setVisibilityAllowed(false);
                leggTilbakeKnapp.setVisibilityAllowed(false);
                target.add(feedbackPanel, sendKnapp, leggTilbakeKnapp);
            } catch (JournalforingFeilet e) {
                send(getPage(), BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
                kvittering.visKvittering(target, getString("dialogpanel.feilmelding.journalforing"),
                        visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
            } catch (Exception e) {
                error(getString("dialogpanel.feilmelding.send.henvendelse"));
                target.add(feedbackPanel);
            } finally {
                timer.stop();
                timer.report();
            }
        }

        private void sendHenvendelse(HenvendelseVM henvendelseVM) throws Exception {
            Meldingstype meldingstype = meldingstype(henvendelseVM.kanal, henvendelseVM.brukerKanSvare);
            Melding melding = new MeldingBuilder()
                    .withHenvendelseVM(henvendelseVM)
                    .withEldsteMeldingITraad(Optional.ofNullable(sporsmal))
                    .withMeldingstype(meldingstype)
                    .withFnr(grunnInfo.bruker.fnr)
                    .withNavident(getSubjectHandler().getUid())
                    .withValgtEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())
                    .build()
                    .withBrukersEnhet(sporsmal.brukersEnhet);

            Optional<Sak> sak = Optional.empty();
            if (melding.meldingstype.equals(SPORSMAL_MODIA_UTGAAENDE) && !henvendelseVM.traadJournalfort) {
                sak = Optional.ofNullable(henvendelseVM.valgtSak);
            }

            henvendelseUtsendingService.ferdigstillHenvendelse(melding, oppgaveId, sak, behandlingsId);
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
