package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
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
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing.JournalforingsPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.conditional.ConditionalUtils.titleAttribute;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.Brukerprofil.BRUKERPROFIL_OPPDATERT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.hentTemagruppeForTema;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NY_DIALOG_AVBRUTT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class NyDialogPanel extends GenericPanel<HenvendelseVM> {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final GrunnInfo grunnInfo;
    private final KvitteringsPanel kvittering;
    private final List<Component> modusKomponenter = new ArrayList<>();
    private final EpostVarselPanel epostVarselPanel;
    private final FeedbackPanel feedbackPanel;
    private final SkrivestottePanel skrivestottePanel;

    public NyDialogPanel(String id, GrunnInfo grunnInfo) {
        super(id, new CompoundPropertyModel<>(new HenvendelseVM()));
        this.grunnInfo = grunnInfo;
        setOutputMarkupPlaceholderTag(true);
        settOppModellMedDefaultVerdier();

        final PropertyModel<Modus> modusModel = new PropertyModel<>(getModel(), "modus");

        final Form<HenvendelseVM> form = new Form<>("nydialogform", getModel());
        form.setOutputMarkupPlaceholderTag(true);

        form.add(lagModusVelger(modusModel));

        epostVarselPanel = new EpostVarselPanel("epostVarsel", modusModel, grunnInfo.bruker.fnr);
        epostVarselPanel.setOutputMarkupPlaceholderTag(true);
        modusKomponenter.add(epostVarselPanel);
        form.add(epostVarselPanel);

        form.add(new Label("navIdent", getSubjectHandler().getUid()));

        JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforing", grunnInfo.bruker.fnr, getModel());
        journalforingsPanel.add(visibleIf(isEqualTo(modusModel, Modus.SPORSMAL)));
        modusKomponenter.add(journalforingsPanel);
        form.add(journalforingsPanel);

        OppgaveTilknytningPanel oppgaveTilknytningPanel = new OppgaveTilknytningPanel("oppgaveTilknytningPanel", getModel(), grunnInfo);
        oppgaveTilknytningPanel.add(visibleIf(isEqualTo(modusModel, Modus.SPORSMAL)));
        modusKomponenter.add(oppgaveTilknytningPanel);
        form.add(oppgaveTilknytningPanel);

        Label tekstfeltOverskrift = new Label("tekstfeltOverskrift", new StringResourceModel("${modus}.tekstfelt", getModel()));
        tekstfeltOverskrift.setOutputMarkupId(true);
        modusKomponenter.add(tekstfeltOverskrift);
        form.add(tekstfeltOverskrift);

        final Label kanalbeskrivelse = new Label("kanalbeskrivelse", new StringResourceModel("${kanal}", getModel(), ""));
        kanalbeskrivelse.setOutputMarkupPlaceholderTag(true);
        modusKomponenter.add(kanalbeskrivelse);
        kanalbeskrivelse.add(visibleIf(isEqualTo(modusModel, Modus.REFERAT)));
        form.add(kanalbeskrivelse);

        EnhancedTextArea tekstfelt = new EnhancedTextArea("tekstfelt", form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(250)
                        .withPlaceholderTextKey("nydialogform.tekstfelt.placeholder")
        );
        Label tekstfeltLabel = new Label("tekstfelt-label", new StringResourceModel("${modus}.overskrift", getModel()));
        tekstfeltLabel.add(new AttributeAppender("for", tekstfelt.get("text").getMarkupId()));
        form.add(tekstfelt, tekstfeltLabel);

        skrivestottePanel = new SkrivestottePanel("skrivestotteContainer", grunnInfo, tekstfelt);
        form.add(skrivestottePanel);
        form.add(new AjaxLink("skrivestotteToggler") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                skrivestottePanel.vis(target);
            }
        });

        RadioGroup<Kanal> radioGroup = lagKanalVelger(modusModel);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kanalbeskrivelse);
            }
        });
        form.add(radioGroup);

        DropDownChoice<Temagruppe> temagruppeVelger = new DropDownChoice<>("temagruppe", Temagruppe.SAMTALEREFERAT, new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        });
        form.add(temagruppeVelger.setRequired(true));
        temagruppeVelger.setOutputMarkupPlaceholderTag(true);
        modusKomponenter.add(temagruppeVelger);
        temagruppeVelger.add(visibleIf(isEqualTo(modusModel, Modus.REFERAT)));

        WebMarkupContainer brukerKanSvareContainer = new WebMarkupContainer("brukerKanSvareContainer");
        brukerKanSvareContainer.setOutputMarkupPlaceholderTag(true);
        brukerKanSvareContainer.add(visibleIf(isEqualTo(modusModel, Modus.SPORSMAL)));
        modusKomponenter.add(brukerKanSvareContainer);
        form.add(brukerKanSvareContainer);

        feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        modusKomponenter.add(feedbackPanel);
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        AjaxButton sendKnapp = getSubmitKnapp(modusModel, form);
        form.add(sendKnapp);
        form.add(getAvbrytKnapp());

        kvittering = new KvitteringsPanel("kvittering");

        add(form, kvittering);
    }

    private AjaxButton getSubmitKnapp(final PropertyModel<Modus> modusModel, final Form<HenvendelseVM> form) {
        AjaxButton submitKnapp = new IndicatingAjaxButtonWithImageUrl("send", "../img/ajaxloader/graa/loader_graa_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                if (modusModel.getObject().equals(Modus.SPORSMAL) && form.getModelObject().valgtSak == null) {
                    error(getString("valgtSak.Required"));
                    onError(target, form);
                } else {
                    sendOgVisKvittering(target, form);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }

        };
        submitKnapp.add(new AttributeModifier("value", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return format(getString("nydialogform.knapp.send"), grunnInfo.bruker.fornavn);
            }
        }));
        return submitKnapp;
    }

    private AjaxLink<Void> getAvbrytKnapp() {
        return new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                settOppModellMedDefaultVerdier();
                send(getPage(), BREADTH, NY_DIALOG_AVBRUTT);
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});"));
    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_VALGT)
    public void oppdaterReferatVM(AjaxRequestTarget target) {
        settOppModellMedDefaultVerdier();
        skrivestottePanel.oppdater(target);
        target.add(this);
    }

    @RunOnEvents(BRUKERPROFIL_OPPDATERT)
    public void oppdaterEpostVarsel(AjaxRequestTarget target) {
        target.add(epostVarselPanel);
    }

    private void settOppModellMedDefaultVerdier() {
        getModelObject().modus = Modus.REFERAT;
        getModelObject().oppgaveTilknytning = OppgaveTilknytning.SAKSBEHANDLER;
        getModelObject().kanal = null;
        getModelObject().valgtSak = null;
        if (saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()) {
            getModelObject().kanal = TELEFON;
        }
    }

    private RadioGroup<Kanal> lagKanalVelger(IModel<Modus> modusModel) {
        RadioGroup<Kanal> radioGroup = new RadioGroup<>("kanal");
        modusKomponenter.add(radioGroup);
        radioGroup.setOutputMarkupPlaceholderTag(true);
        radioGroup.setRequired(true);
        radioGroup.add(visibleIf(isEqualTo(modusModel, Modus.REFERAT)));
        radioGroup.add(new ListView<Kanal>("kanalvalg", TELEFON_OG_OPPMOTE) {
            @Override
            protected void populateItem(ListItem<Kanal> item) {
                String kanalType = item.getModelObject().name();

                item.add(titleAttribute(getString(kanalType)));

                Radio<Kanal> kanalKnapp = new Radio<>("kanalknapp", item.getModel());
                kanalKnapp.add(new AttributeAppender("aria-label", getString(kanalType)));

                Component kanalIkon = new WebMarkupContainer("kanalikon").add(cssClass(kanalType.toLowerCase()));

                WebMarkupContainer kanalknappLabel = new WebMarkupContainer("kanalknapp-label");
                kanalknappLabel.add(new AttributeAppender("for", kanalKnapp.getMarkupId()));

                Label kanalknappLabelTekst = new Label("kanalknapp-label-tekst", getString(kanalType));

                kanalknappLabel.add(kanalknappLabelTekst, kanalIkon);
                item.add(kanalKnapp, kanalknappLabel);
            }
        });

        return radioGroup;
    }

    private Component lagModusVelger(PropertyModel<Modus> modusModel) {
        RadioChoice<Modus> velger = new RadioChoice<>("velgModus", modusModel, asList(Modus.values()));
        velger.setSuffix("");
        velger.setChoiceRenderer(new ChoiceRenderer<Modus>() {
            @Override
            public Object getDisplayValue(Modus object) {
                return getString(object.name() + ".tab");
            }
        });
        velger.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(modusKomponenter.toArray(new Component[modusKomponenter.size()]));
                target.appendJavaScript("$('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});");
            }
        });
        return velger;
    }

    private void sendOgVisKvittering(AjaxRequestTarget target, Form<HenvendelseVM> form) {
        try {
            switch (getModelObject().modus) {
                case REFERAT:
                    sendReferat();
                    break;
                case SPORSMAL:
                    sendSporsmal();
                    break;
            }
            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
            kvittering.visKvittering(target, getString(getModelObject().getKvitteringsTekstKeyBasertPaaModus("nydialogpanel")), form);
        } catch (JournalforingFeilet e) {
            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER));
            kvittering.visKvittering(target, getString("dialogpanel.feilmelding.journalforing"), form);
        } catch (Exception e) {
            error(getString("dialogpanel.feilmelding.send.henvendelse"));
            target.add(feedbackPanel);
        }
    }

    private void sendReferat() throws Exception {
        sendHenvendelse(getModelObject(), referatType(getModelObject().kanal), Optional.<Melding>none());
    }

    private Meldingstype referatType(Kanal kanal) {
        return kanal == OPPMOTE ? SAMTALEREFERAT_OPPMOTE : SAMTALEREFERAT_TELEFON;
    }

    private void sendSporsmal() throws Exception {
        HenvendelseVM henvendelseVM = getModelObject();
        henvendelseVM.temagruppe = Temagruppe.valueOf(hentTemagruppeForTema(henvendelseVM.valgtSak.temaKode));
        henvendelseVM.kanal = Kanal.TEKST;
        sendHenvendelse(henvendelseVM, SPORSMAL_MODIA_UTGAAENDE, Optional.<Melding>none());
    }

    private void sendHenvendelse(HenvendelseVM henvendelseVM, Meldingstype meldingstype, Optional<Melding> eldsteMeldingITraad) throws Exception {
        Melding melding = new MeldingBuilder()
                .withHenvendelseVM(henvendelseVM)
                .withEldsteMeldingITraad(eldsteMeldingITraad)
                .withMeldingstype(meldingstype)
                .withFnr(grunnInfo.bruker.fnr)
                .withNavident(getSubjectHandler().getUid())
                .withValgtEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())
                .build();

        Optional<Sak> sak = none();
        if (melding.meldingstype.equals(SPORSMAL_MODIA_UTGAAENDE) && !henvendelseVM.traadJournalfort) {
            sak = optional(henvendelseVM.valgtSak);
        }

        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), sak);
    }

}
