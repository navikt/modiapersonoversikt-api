package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.SvarOgReferatVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.titleAttribute;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.OPPMOTE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TEKST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TELEFON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.AnimasjonsUtils.animertVisningToggle;

public class SvarPanel extends Panel {

    public static final String SVAR_AVBRUTT = "svar.avbrutt";

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    protected OppgaveBehandlingService oppgaveBehandlingService;

    private final String fnr;
    private final Optional<String> oppgaveId;
    private final Sporsmal sporsmal;
    private final WebMarkupContainer traadContainer, svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;
    private final KvitteringsPanel kvittering;
    private final WebMarkupContainer visTraadContainer;

    public SvarPanel(String id, String fnr, Sporsmal sporsmal, final List<SvarEllerReferat> svar, Optional<String> oppgaveId) {
        super(id);
        this.fnr = fnr;
        this.oppgaveId = oppgaveId;
        this.sporsmal = sporsmal;
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
                new ListView<SvarEllerReferat>("svarliste", svar) {
                    @Override
                    protected void populateItem(ListItem<SvarEllerReferat> item) {
                        SvarEllerReferat svarEllerReferat = item.getModelObject();
                        String type = svarEllerReferat.type.name().substring(0, svarEllerReferat.type.name().lastIndexOf("_")).toLowerCase();
                        item.add(new TidligereMeldingPanel("svar", type, svarEllerReferat.temagruppe, svarEllerReferat.opprettetDato, svarEllerReferat.fritekst, svarEllerReferat.navIdent, true));
                    }
                }
        );

        svarContainer.setOutputMarkupId(true);
        svarContainer.add(
                new SvarForm("svarform", lagModelObjectMedKanalOgTemagruppe()),
                new AjaxLink<Void>("leggtilbake") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (svar.isEmpty()) {
                            traadContainer.setVisibilityAllowed(true);
                            animertVisningToggle(target, svarContainer);
                            animertVisningToggle(target, leggTilbakePanel);
                            target.add(SvarPanel.this);
                        } else {
                            send(SvarPanel.this, Broadcast.BUBBLE, SVAR_AVBRUTT);
                        }
                    }
                }.add(new Label("leggtilbaketekst", new ResourceModel("svarpanel.avbryt." + (svar.isEmpty() ? "leggtilbake" : "avbryt"))))
        );

        leggTilbakePanel.setVisibilityAllowed(false);

        add(visTraadContainer, traadContainer, svarContainer, leggTilbakePanel, kvittering);
    }

    private SvarOgReferatVM lagModelObjectMedKanalOgTemagruppe() {
        SvarOgReferatVM svarOgReferatVM = new SvarOgReferatVM();
        svarOgReferatVM.kanal = TEKST;
        svarOgReferatVM.temagruppe = getTemagruppeFraSporsmal();
        return svarOgReferatVM;
    }

    //Denne er midlertidig mens vi venter på full integrasjon med kodeverk
    private Temagruppe getTemagruppeFraSporsmal() {
        for (Temagruppe temagruppe : Temagruppe.values()) {
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
    }

    private class SvarForm extends Form<SvarOgReferatVM> {

        public SvarForm(String id, SvarOgReferatVM svarOgReferatVM) {
            super(id, new CompoundPropertyModel<>(svarOgReferatVM));

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

            final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
            feedbackPanel.setOutputMarkupId(true);
            add(feedbackPanel);

            add(new AjaxButton("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                    sendOgVisKvittering(SvarForm.this.getModelObject(), target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            });
        }

        private void sendOgVisKvittering(SvarOgReferatVM svarOgReferatVM, AjaxRequestTarget target) {
            sendHenvendelse(svarOgReferatVM);
            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(MELDING_SENDT_TIL_BRUKER));
            kvittering.visISekunder(3, getString(svarOgReferatVM.kanal.getKvitteringKey("svarpanel")), target,
                    visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
        }

        private void sendHenvendelse(SvarOgReferatVM svarOgReferatVM) {
            SvarEllerReferat svarEllerReferat = new SvarEllerReferat()
                    .withFnr(fnr)
                    .withNavIdent(getSubjectHandler().getUid())
                    .withSporsmalsId(sporsmal.id)
                    .withTemagruppe(svarOgReferatVM.temagruppe.name())
                    .withKanal(svarOgReferatVM.kanal.name())
                    .withType(svarType(svarOgReferatVM.kanal))
                    .withFritekst(svarOgReferatVM.getFritekst())
                    .withKontorsperretEnhet(sporsmal.konorsperretEnhet);

            henvendelseUtsendingService.sendSvarEllerReferat(svarEllerReferat);
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId);
        }

        private Henvendelsetype svarType(Kanal kanal) {
            Henvendelsetype henvendelsetype = null;
            if (kanal == TEKST) {
                henvendelsetype = Henvendelsetype.SVAR_SKRIFTLIG;
            } else if (kanal == OPPMOTE) {
                henvendelsetype = Henvendelsetype.SVAR_OPPMOTE;
            } else if (kanal == TELEFON) {
                henvendelsetype = Henvendelsetype.SVAR_TELEFON;
            }
            return henvendelsetype;
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(OnDomReadyHeaderItem.forScript("$('#" + get("tekstfelt:text").getMarkupId() + "').focus();"));
        }
    }

}
