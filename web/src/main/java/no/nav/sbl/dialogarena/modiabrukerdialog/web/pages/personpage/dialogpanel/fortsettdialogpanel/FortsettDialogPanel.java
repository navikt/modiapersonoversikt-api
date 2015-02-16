package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.*;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
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
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.SVAR_AVBRUTT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SVAR_TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService.OppgaveErFerdigstilt;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class FortsettDialogPanel extends GenericPanel<HenvendelseVM> {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final GrunnInfo grunnInfo;
    private final Optional<String> oppgaveId;
    private final Melding sporsmal;
    private final List<Melding> svar;
    private final WebMarkupContainer traadContainer, svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;
    private final KvitteringsPanel kvittering;
    private final WebMarkupContainer visTraadContainer;
    private final AjaxLink<Void> leggTilbakeKnapp;

    public FortsettDialogPanel(String id, GrunnInfo grunnInfo, final List<Melding> traad, Optional<String> oppgaveId) {
        super(id, new CompoundPropertyModel<>(new HenvendelseVM()));
        this.grunnInfo = grunnInfo;
        this.oppgaveId = oppgaveId;
        this.sporsmal = traad.get(0);
        this.svar = new ArrayList<>(traad.subList(1, traad.size()));
        settOppModellMedDefaultKanalOgTemagruppe(getModelObject());
        setOutputMarkupId(true);

        visTraadContainer = new WebMarkupContainer("vistraadcontainer");
        traadContainer = new WebMarkupContainer("traadcontainer");
        svarContainer = new WebMarkupContainer("svarcontainer");
        leggTilbakePanel = new LeggTilbakePanel("leggtilbakepanel", sporsmal.temagruppe, oppgaveId);
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

        leggTilbakeKnapp = new AjaxLink<Void>("leggtilbake") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (svar.isEmpty()) {
                    traadContainer.setVisibilityAllowed(true);
                    animertVisningToggle(target, svarContainer);
                    animertVisningToggle(target, leggTilbakePanel);
                    leggTilbakePanel.add(AttributeModifier.replace("aria-expanded", "true"));
                    target.add(FortsettDialogPanel.this);
                    target.focusComponent(leggTilbakePanel.hentForsteFokusKomponent());
                } else {
                    send(getPage(), BREADTH, SVAR_AVBRUTT);
                }
            }
        };
        if (svar.isEmpty()) {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.avbryt.leggtilbake")));
            leggTilbakeKnapp.add(AttributeModifier.replace("aria-controls", leggTilbakePanel.getMarkupId()));
        } else {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("fortsettdialogpanel.avbryt.avbryt")));
        }

        svarContainer.setOutputMarkupId(true);
        svarContainer.add(new FortsettDialogForm("fortsettdialogform", grunnInfo, getModel()), leggTilbakeKnapp);

        leggTilbakePanel.setVisibilityAllowed(false);

        add(visTraadContainer, traadContainer, svarContainer, leggTilbakePanel, kvittering);
    }

    private void settOppModellMedDefaultKanalOgTemagruppe(HenvendelseVM henvendelseVM) {
        henvendelseVM.kanal = TEKST;
        henvendelseVM.temagruppe = Temagruppe.valueOf(sporsmal.temagruppe);
    }

    @RunOnEvents(LeggTilbakePanel.LEGG_TILBAKE_AVBRUTT)
    public void skjulLeggTilbakePanel(AjaxRequestTarget target) {
        animertVisningToggle(target, svarContainer);
        animertVisningToggle(target, leggTilbakePanel);
        target.add(this);
        target.focusComponent(leggTilbakeKnapp);
    }

    private class FortsettDialogForm extends Form<HenvendelseVM> {

        private final FeedbackPanel feedbackPanel;
        private final AjaxButton sendKnapp;

        public FortsettDialogForm(String id, final GrunnInfo grunnInfo, final IModel<HenvendelseVM> henvendelseVMModel) {
            super(id, henvendelseVMModel);
            final HenvendelseVM henvendelseVM = henvendelseVMModel.getObject();

            add(new Label("navIdent", getSubjectHandler().getUid()));
            add(new FortsettDialogFormElementer("fortsettdialogformelementer", grunnInfo.fnr, getModel()));

            feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
            feedbackPanel.setOutputMarkupId(true);
            add(feedbackPanel);

            sendKnapp = new IndicatingAjaxButtonWithImageUrl("send", "../img/ajaxloader/graa/loader_graa_48.gif") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (henvendelseVM.brukerKanSvareSkalEnables().getObject() && henvendelseVM.brukerKanSvare && henvendelseVM.valgtSak == null) {
                        error(getString("valgtSak.Required"));
                        onError(target, form);
                    } else {
                        sendOgVisKvittering(henvendelseVM, target);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };
            sendKnapp.add(new AttributeModifier("value", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    return format(getString("fortsettdialogform.knapp.send"), grunnInfo.fornavn);
                }
            }));
            add(sendKnapp);
        }

        private void sendOgVisKvittering(HenvendelseVM henvendelseVM, AjaxRequestTarget target) {
            try {
                sendHenvendelse(henvendelseVM);
                send(getPage(), BREADTH, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
                kvittering.visKvittering(target, getString(henvendelseVM.getKvitteringsTekstKeyBasertPaaBrukerKanSvare("fortsettdialogpanel")),
                        visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
            } catch (OppgaveErFerdigstilt oppgaveErFerdigstilt) {
                error(getString("fortsettdialogform.feilmelding.oppgaveferdigstilt"));
                sendKnapp.setVisibilityAllowed(false);
                leggTilbakeKnapp.setVisibilityAllowed(false);
                target.add(feedbackPanel, sendKnapp, leggTilbakeKnapp);
            }
        }

        private void sendHenvendelse(HenvendelseVM henvendelseVM) throws OppgaveErFerdigstilt {
            Meldingstype meldingstype = meldingstype(henvendelseVM.kanal, henvendelseVM.brukerKanSvare);
            Melding melding = new Melding()
                    .withFnr(grunnInfo.fnr)
                    .withNavIdent(getSubjectHandler().getUid())
                    .withTraadId(sporsmal.id)
                    .withTemagruppe(henvendelseVM.temagruppe.name())
                    .withKanal(henvendelseVM.kanal.name())
                    .withType(meldingstype)
                    .withFritekst(henvendelseVM.getFritekst())
                    .withKontorsperretEnhet(sporsmal.kontorsperretEnhet)
                    .withEksternAktor(getSubjectHandler().getUid())
                    .withTilknyttetEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());

            melding = henvendelseUtsendingService.sendHenvendelse(melding, oppgaveId);
            if (meldingstype.equals(SPORSMAL_MODIA_UTGAAENDE)) {
                behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                        melding.traadId,
                        henvendelseVM.valgtSak.saksId,
                        henvendelseVM.valgtSak.temaKode,
                        saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()
                );
            }
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId);
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

    }

}
