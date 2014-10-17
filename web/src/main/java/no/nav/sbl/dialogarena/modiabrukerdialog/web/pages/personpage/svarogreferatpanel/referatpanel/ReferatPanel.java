package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.SvarOgReferatVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.titleAttribute;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TELEFON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TELEFON_OG_OPPMOTE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype.REFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype.REFERAT_TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT;

public class ReferatPanel extends Panel {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final String fnr;
    private final KvitteringsPanel kvittering;
    private final SvarOgReferatVM svarOgReferatVM;

    public ReferatPanel(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        setOutputMarkupPlaceholderTag(true);

        this.svarOgReferatVM = new SvarOgReferatVM();
        final Form<SvarOgReferatVM> form = new Form<>("referatform", new CompoundPropertyModel<>(vmMedDefaultVerdier()));
        form.setOutputMarkupPlaceholderTag(true);

        form.add(new EnhancedTextArea("tekstfelt", form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(250)
                        .withPlaceholderTextKey("referatform.tekstfelt.placeholder")
        ));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        final RadioGroup<Kanal> radioGroup = new RadioGroup<>("kanal");
        radioGroup.setOutputMarkupId(true);
        radioGroup.setRequired(true);
        radioGroup.add(new ListView<Kanal>("kanalvalg", TELEFON_OG_OPPMOTE) {
            @Override
            protected void populateItem(ListItem<Kanal> item) {
                String kanalType = item.getModelObject().name();

                item.add(titleAttribute(getString(kanalType)));

                Radio<Kanal> kanalKnapp = new Radio<>("kanalknapp", item.getModel());
                kanalKnapp.add(new AttributeAppender("aria-label", getString(kanalType)));

                Component kanalIkon = new WebMarkupContainer("kanalikon").add(cssClass(kanalType.toLowerCase()));

                item.add(kanalKnapp, kanalIkon);
            }
        });

        form.add(radioGroup);

        final Label kanalbeskrivelse = new Label("kanalbeskrivelse", new StringResourceModel("${name}", radioGroup.getModel(), ""));
        kanalbeskrivelse.setOutputMarkupId(true);
        form.add(kanalbeskrivelse);

        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kanalbeskrivelse);
            }
        });

        form.add(new DropDownChoice<>("temagruppe", asList(Temagruppe.values()), new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        }).setRequired(true));

        form.add(new AjaxButton("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                sendOgVisKvittering(target, form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        kvittering = new KvitteringsPanel("kvittering");

        add(form, kvittering);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});"));
    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_VALGT)
    public void oppdaterReferatVM(AjaxRequestTarget target) {
        vmMedDefaultVerdier();
        target.add(this);
    }

    private SvarOgReferatVM vmMedDefaultVerdier() {
        if (saksbehandlerInnstillingerService.valgtEnhetErKontaktsenter()) {
            svarOgReferatVM.kanal = TELEFON;
        }
        return svarOgReferatVM;
    }

    private void sendOgVisKvittering(AjaxRequestTarget target, Form<SvarOgReferatVM> form) {
        sendHenvendelse(svarOgReferatVM);
        send(getPage(), Broadcast.BREADTH, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
        kvittering.visISekunder(3, getString(svarOgReferatVM.kanal.getKvitteringKey("referatpanel")), target, form);
    }

    private void sendHenvendelse(SvarOgReferatVM svarOgReferatVM) {
        SvarEllerReferat referat = new SvarEllerReferat()
                .withFnr(fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withTemagruppe(svarOgReferatVM.temagruppe.name())
                .withKanal(svarOgReferatVM.kanal.name())
                .withType(referatType(svarOgReferatVM.kanal))
                .withFritekst(svarOgReferatVM.getFritekst())
                .withSporsmalsId(null);

        henvendelseUtsendingService.sendSvarEllerReferat(referat);
    }

    private Henvendelsetype referatType(Kanal kanal) {
        return kanal == OPPMOTE ? REFERAT_OPPMOTE : REFERAT_TELEFON;
    }

}
