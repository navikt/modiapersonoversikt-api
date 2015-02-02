package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.LokaltKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.referatpanel.journalforing.JournalforingsPanel;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
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
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.titleAttribute;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT;

public class ReferatPanel extends GenericPanel<HenvendelseVM> {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private LokaltKodeverk lokaltKodeverk;

    private final GrunnInfo grunnInfo;
    private final KvitteringsPanel kvittering;
    private final List<Component> modusKomponenter = new ArrayList<>();

    public ReferatPanel(String id, GrunnInfo grunnInfo) {
        super(id, new CompoundPropertyModel<>(new HenvendelseVM()));
        this.grunnInfo = grunnInfo;
        setOutputMarkupPlaceholderTag(true);

        settOppModellMedDefaultVerdier();

        final PropertyModel<Modus> modusModel = new PropertyModel<>(getModel(), "modus");

        final Form<HenvendelseVM> form = new Form<>("referatform", getModel());
        form.setOutputMarkupPlaceholderTag(true);

        form.add(lagModusVelger(modusModel));

        Component epostVarsel = new EpostVarselPanel("epostVarsel", modusModel, grunnInfo.fnr);
        epostVarsel.setOutputMarkupPlaceholderTag(true);
        modusKomponenter.add(epostVarsel);
        form.add(epostVarsel);

        Label overskrift = new Label("overskrift", new StringResourceModel("${modus}.overskrift", getModel()));
        overskrift.setOutputMarkupId(true);
        modusKomponenter.add(overskrift);

        JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforing", grunnInfo.fnr, getModel());
        journalforingsPanel.add(visibleIf(isEqualTo(modusModel, Modus.SPORSMAL)));
        modusKomponenter.add(journalforingsPanel);
        form.add(journalforingsPanel);

        form.add(overskrift);
        form.add(new Label("navIdent", getSubjectHandler().getUid()));

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
                        .withPlaceholderTextKey("referatform.tekstfelt.placeholder")
        );
        Label tekstfeltLabel = new Label("tekstfelt-label", new StringResourceModel("${modus}.overskrift", getModel()));
        tekstfeltLabel.add(new AttributeAppender("for", tekstfelt.get("text").getMarkupId()));
        form.add(tekstfelt, tekstfeltLabel);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        modusKomponenter.add(feedbackPanel);
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        RadioGroup<Kanal> radioGroup = lagKanalVelger(modusModel);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kanalbeskrivelse);
            }
        });
        form.add(radioGroup);

        DropDownChoice<Temagruppe> temagruppeVelger = new DropDownChoice<>("temagruppe", Temagruppe.UTGAAENDE, new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        });
        form.add(temagruppeVelger.setRequired(true));
        temagruppeVelger.setOutputMarkupPlaceholderTag(true);
        modusKomponenter.add(temagruppeVelger);
        temagruppeVelger.add(visibleIf(isEqualTo(modusModel, Modus.REFERAT)));

        form.add(getSubmitKnapp(modusModel, form, feedbackPanel));
        form.add(getAvbrytKnapp());

        kvittering = new KvitteringsPanel("kvittering");

        add(form, kvittering);
    }

    private AjaxButton getSubmitKnapp(final PropertyModel<Modus> modusModel, final Form<HenvendelseVM> form, final FeedbackPanel feedbackPanel) {
        AjaxButton submitKnapp = new AjaxButton("send") {
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
                return format(getString("referatform.knapp.send"), grunnInfo.fornavn);
            }
        }));
        return submitKnapp;
    }

    private AjaxLink<Void> getAvbrytKnapp() {
        return new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                settOppModellMedDefaultVerdier();
                target.add(ReferatPanel.this);
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
        target.add(this);
    }

    private void settOppModellMedDefaultVerdier() {
        getModelObject().modus = Modus.REFERAT;
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
        switch (getModelObject().modus) {
            case REFERAT:
                sendReferat();
                kvittering.visKvittering(target, getString(getModelObject().kanal.getKvitteringKey("referatpanel")), form);
                break;
            case SPORSMAL:
                sendSporsmal();
                kvittering.visKvittering(target, getString("referatpanel.sporsmal.kvittering.bekreftelse"), form);
                break;
        }
        send(getPage(), Broadcast.BREADTH, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
    }

    private void sendReferat() {
        Melding referat = felles()
                .withKanal(getModelObject().kanal.name())
                .withTemagruppe(getModelObject().temagruppe.name())
                .withType(referatType(getModelObject().kanal));
        henvendelseUtsendingService.sendHenvendelse(referat);
    }

    private void sendSporsmal() {
        HenvendelseVM henvendelseVM = getModelObject();
        Melding sporsmal = felles()
                .withKanal(Kanal.TEKST.name())
                .withType(SPORSMAL_MODIA_UTGAAENDE)
                .withTemagruppe(lokaltKodeverk.hentTemagruppeForTema(henvendelseVM.valgtSak.temaKode))
                .withTilknyttetEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());

        sporsmal = henvendelseUtsendingService.sendHenvendelse(sporsmal);

        behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                sporsmal.traadId,
                henvendelseVM.valgtSak.saksId,
                henvendelseVM.valgtSak.temaKode,
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()
        );
    }

    private Melding felles() {
        return new Melding()
                .withFnr(grunnInfo.fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withFritekst(getModelObject().getFritekst())
                .withEksternAktor(getSubjectHandler().getUid());
    }

    private Meldingstype referatType(Kanal kanal) {
        return kanal == OPPMOTE ? Meldingstype.SAMTALEREFERAT_OPPMOTE : Meldingstype.SAMTALEREFERAT_TELEFON;
    }

}
