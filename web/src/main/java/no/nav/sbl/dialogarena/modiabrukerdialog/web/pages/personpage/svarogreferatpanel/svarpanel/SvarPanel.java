package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.SvarOgReferatVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
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
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
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
                        String type = svarEllerReferat.type.name().toLowerCase();
                        item.add(new TidligereMeldingPanel("svar", type, item.getModelObject().temagruppe, item.getModelObject().opprettetDato, item.getModelObject().fritekst, true));
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
        svarOgReferatVM.kanal = SvarKanal.TEKST;
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

            final RadioGroup<SvarKanal> radioGroup = new RadioGroup<>("kanal");
            radioGroup.setRequired(true);
            radioGroup.add(new ListView<SvarKanal>("kanalvalg", asList(SvarKanal.values())) {
                @Override
                protected void populateItem(ListItem<SvarKanal> item) {
                    item.add(new Radio<>("kanalknapp", item.getModel()));
                    item.add(new WebMarkupContainer("kanalikon").add(cssClass(item.getModelObject().name().toLowerCase())));
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
            kvittering.visISekunder(3, getString(svarOgReferatVM.kanal.getKvitteringKey()), target,
                    visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
        }

        private void sendHenvendelse(SvarOgReferatVM svarOgReferatVM) {
            SvarEllerReferat svarEllerReferat = new SvarEllerReferat()
                    .withFnr(fnr)
                    .withNavIdent(getSubjectHandler().getUid())
                    .withSporsmalsId(sporsmal.id)
                    .withTemagruppe(svarOgReferatVM.temagruppe.name())
                    .withKanal(svarOgReferatVM.kanal.name())
                    .withFritekst(svarOgReferatVM.getFritekst());

            if (svarOgReferatVM.kanal.equals(SvarKanal.TEKST)) {
                henvendelseUtsendingService.sendSvar(svarEllerReferat);
            } else {
                henvendelseUtsendingService.sendReferat(svarEllerReferat);
            }
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId);
        }
    }

}
