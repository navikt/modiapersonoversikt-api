package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.svarpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.titleAttribute;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService.OppgaveErFerdigstilt;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class SvarPanel extends Panel {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;

    private final GrunnInfo grunnInfo;
    private final Optional<String> oppgaveId;
    private final Melding sporsmal;
    private final List<Melding> svar;
    private final WebMarkupContainer traadContainer, svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;
    private final KvitteringsPanel kvittering;
    private final WebMarkupContainer visTraadContainer;
    private final AjaxLink<Void> leggTilbakeKnapp;

    public SvarPanel(String id, GrunnInfo grunnInfo, final List<Melding> traad, Optional<String> oppgaveId) {
        super(id);
        this.grunnInfo = grunnInfo;
        this.oppgaveId = oppgaveId;
        this.sporsmal = traad.get(0);
        this.svar = new ArrayList<>(traad.subList(1, traad.size()));
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
                    target.add(SvarPanel.this);
                    target.focusComponent(leggTilbakePanel.hentForsteFokusKomponent());
                } else {
                    send(getPage(), BREADTH, Events.SporsmalOgSvar.SVAR_AVBRUTT);
                }
            }
        };
        if (svar.isEmpty()) {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("svarpanel.avbryt.leggtilbake")));
            leggTilbakeKnapp.add(AttributeModifier.replace("aria-controls", leggTilbakePanel.getMarkupId()));
        } else {
            leggTilbakeKnapp.add(new Label("leggtilbaketekst", new ResourceModel("svarpanel.avbryt.avbryt")));
        }

        svarContainer.setOutputMarkupId(true);
        svarContainer.add(new SvarForm("svarform", lagModelObjectMedKanalOgTemagruppe()), leggTilbakeKnapp);

        leggTilbakePanel.setVisibilityAllowed(false);

        add(visTraadContainer, traadContainer, svarContainer, leggTilbakePanel, kvittering);
    }

    private HenvendelseVM lagModelObjectMedKanalOgTemagruppe() {
        HenvendelseVM henvendelseVM = new HenvendelseVM();
        henvendelseVM.kanal = TEKST;
        henvendelseVM.temagruppe = getTemagruppeFraSporsmal();
        return henvendelseVM;
    }

    private Temagruppe getTemagruppeFraSporsmal() {
        for (Temagruppe temagruppe : Temagruppe.INNGAAENDE) {
            if (temagruppe.name().equals(sporsmal.temagruppe)) {
                return temagruppe;
            }
        }
        return Temagruppe.ARBD; //Bruker denne som default
    }

    @RunOnEvents(LeggTilbakePanel.LEGG_TILBAKE_AVBRUTT)
    public void skjulLeggTilbakePanel(AjaxRequestTarget target) {
        animertVisningToggle(target, svarContainer);
        animertVisningToggle(target, leggTilbakePanel);
        target.add(this);
        target.focusComponent(leggTilbakeKnapp);
    }

    private class SvarForm extends Form<HenvendelseVM> {

        private final FeedbackPanel feedbackPanel;
        private final AjaxButton sendKnapp;

        public SvarForm(String id, HenvendelseVM henvendelseVM) {
            super(id, new CompoundPropertyModel<>(henvendelseVM));

            final RadioGroup<Kanal> radioGroup = new RadioGroup<>("kanal");
            radioGroup.setRequired(true);
            radioGroup.add(new ListView<Kanal>("kanalvalg", asList(Kanal.values())) {
                @Override
                protected void populateItem(ListItem<Kanal> item) {
                    String kanalType = item.getModelObject().name();

                    item.add(titleAttribute(getString(kanalType)));

                    Radio<Kanal> kanalKnapp = new Radio<>("kanalknapp", item.getModel());
                    kanalKnapp.add(new AttributeAppender("aria-label", getString(kanalType)));

                    Component kanalIkon = new WebMarkupContainer("kanalikon").add(cssClass(kanalType.toLowerCase()));

                    WebMarkupContainer kanalknallLabel = new WebMarkupContainer("kanalknapp-label");
                    kanalknallLabel.add(new AttributeAppender("for", kanalKnapp.getMarkupId()));

                    Label kanalknallLabelTekst = new Label("kanalknapp-label-tekst", getString(kanalType));

                    kanalknallLabel.add(kanalknallLabelTekst, kanalIkon);
                    item.add(kanalKnapp, kanalknallLabel);
                }
            });
            add(radioGroup);

            add(new CheckBox("brukerKanSvare").setOutputMarkupId(true));

            final Label kanalbeskrivelse = new Label("kanalbeskrivelse", new StringResourceModel("${name}.beskrivelse", radioGroup.getModel()));
            kanalbeskrivelse.setOutputMarkupId(true);
            add(kanalbeskrivelse);

            radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    target.add(kanalbeskrivelse);
                }
            });

            add(new Label("navIdent", getSubjectHandler().getUid()));

            add(new EnhancedTextArea("tekstfelt", getModel(),
                    new EnhancedTextAreaConfigurator()
                            .withMaxCharCount(5000)
                            .withMinTextAreaHeight(150)
                            .withPlaceholderTextKey("svarform.tekstfelt.placeholder")
            ));

            feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
            feedbackPanel.setOutputMarkupId(true);
            add(feedbackPanel);

            sendKnapp = new AjaxButton("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                    sendOgVisKvittering(SvarForm.this.getModelObject(), target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };
            sendKnapp.add(new AttributeModifier("value", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    return format(getString("svarform.knapp.send"), grunnInfo.fornavn);
                }
            }));
            add(sendKnapp);
        }

        private void sendOgVisKvittering(HenvendelseVM henvendelseVM, AjaxRequestTarget target) {
            try {
                sendHenvendelse(henvendelseVM);
                send(getPage(), BREADTH, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
                kvittering.visKvittering(target, getString(henvendelseVM.kanal.getKvitteringKey("svarpanel")),
                        visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
            } catch (OppgaveErFerdigstilt oppgaveErFerdigstilt) {
                error(getString("svarform.feilmelding.oppgaveferdigstilt"));
                sendKnapp.setVisibilityAllowed(false);
                leggTilbakeKnapp.setVisibilityAllowed(false);
                target.add(feedbackPanel, sendKnapp, leggTilbakeKnapp);
            }
        }

        private void sendHenvendelse(HenvendelseVM henvendelseVM) throws OppgaveErFerdigstilt {
            Melding melding = new Melding()
                    .withFnr(grunnInfo.fnr)
                    .withNavIdent(getSubjectHandler().getUid())
                    .withTraadId(sporsmal.id)
                    .withTemagruppe(henvendelseVM.temagruppe.name())
                    .withKanal(henvendelseVM.kanal.name())
                    .withType(meldingstype(henvendelseVM.kanal, henvendelseVM.brukerKanSvare))
                    .withFritekst(henvendelseVM.getFritekst())
                    .withKontorsperretEnhet(sporsmal.kontorsperretEnhet)
                    .withEksternAktor(getSubjectHandler().getUid());

            henvendelseUtsendingService.sendHenvendelse(melding, oppgaveId);
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId);
        }

        private Meldingstype meldingstype(Kanal kanal, boolean brukerKanSvare) {

            if (brukerKanSvare) {
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
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(OnDomReadyHeaderItem.forScript("$('#" + get("tekstfelt:text").getMarkupId() + "').focus();"));
        }
    }

}
