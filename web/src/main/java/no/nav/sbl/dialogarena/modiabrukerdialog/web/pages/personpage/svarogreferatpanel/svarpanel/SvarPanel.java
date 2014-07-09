package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.SvarOgReferatVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class SvarPanel extends Panel {

    @Inject
    private SakService sakService;

    private final String fnr;
    private final Sporsmal sporsmal;
    private final WebMarkupContainer traadContainer, svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;
    private final KvitteringsPanel kvittering;
    private final WebMarkupContainer visTraadContainer;

    public SvarPanel(String id, String fnr, Sporsmal sporsmal, List<Svar> svar) {
        super(id);
        this.fnr = fnr;
        this.sporsmal = sporsmal;
        setOutputMarkupId(true);

        visTraadContainer = new WebMarkupContainer("visTraadContainer");
        visTraadContainer.setVisibilityAllowed(!svar.isEmpty());
        visTraadContainer
                .add(new WebMarkupContainer("ekspanderingspil").add(hasCssClassIf("ekspandert", new AbstractReadOnlyModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return traadContainer.isVisibilityAllowed();
                    }
                })))
                .add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        traadContainer.setVisibilityAllowed(!traadContainer.isVisibilityAllowed());
                        target.add(visTraadContainer, traadContainer);
                    }
                });


        traadContainer = new WebMarkupContainer("traadcontainer");
        traadContainer.add(new TidligereMeldingPanel("sporsmal", sporsmal.temagruppe, sporsmal.opprettetDato, sporsmal.fritekst, !svar.isEmpty()));
        traadContainer.add(new ListView<Svar>("svarliste", svar) {
            @Override
            protected void populateItem(ListItem<Svar> item) {
                item.add(new TidligereMeldingPanel("svar", item.getModelObject().temagruppe, item.getModelObject().opprettetDato, item.getModelObject().fritekst, true));
            }
        });
        traadContainer.setVisibilityAllowed(svar.isEmpty());
        traadContainer.setOutputMarkupPlaceholderTag(true);

        svarContainer = new WebMarkupContainer("svarcontainer");
        svarContainer.setOutputMarkupId(true);
        svarContainer.add(
                lagSvarForm(),
                new AjaxLink<Void>("leggtilbake") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        svarContainer.setVisibilityAllowed(false);
                        traadContainer.setVisibilityAllowed(true);
                        leggTilbakePanel.setVisibilityAllowed(true);
                        target.add(SvarPanel.this);
                    }
                });

        leggTilbakePanel = new LeggTilbakePanel("leggtilbakepanel", sporsmal);
        leggTilbakePanel.setVisibilityAllowed(false);

        kvittering = new KvitteringsPanel("kvittering");

        add(visTraadContainer, traadContainer, svarContainer, leggTilbakePanel, kvittering);
    }

    private Form lagSvarForm() {
        final Form<SvarOgReferatVM> form = new Form<>("svarform", new CompoundPropertyModel<>(lagModelObjectMedKanalOgTemagruppe()));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(form));
        feedbackPanel.setOutputMarkupId(true);

        final RadioGroup<SvarKanal> radioGroup = new RadioGroup<>("kanal");
        radioGroup.setRequired(true);
        radioGroup.add(new ListView<SvarKanal>("kanalvalg", asList(SvarKanal.values())) {
            @Override
            protected void populateItem(ListItem<SvarKanal> item) {
                item.add(new Radio<>("kanalknapp", item.getModel()));
                item.add(new WebMarkupContainer("kanalikon").add(cssClass(item.getModelObject().name().toLowerCase())));
            }
        });
        form.add(radioGroup);

        final Label kanalbeskrivelse = new Label("kanalbeskrivelse", new StringResourceModel("${name}.beskrivelse", radioGroup.getModel()));
        kanalbeskrivelse.setOutputMarkupId(true);

        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kanalbeskrivelse);
            }
        });

        form.add(
                new Label("navIdent", getSubjectHandler().getUid()),
                kanalbeskrivelse,
                feedbackPanel
        );

        form.add(new EnhancedTextArea("tekstfelt", form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(150)
                        .withPlaceholderText(getString("svarform.tekstfelt.placeholder"))));


        form.add(new AjaxButton("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                sendOgVisKvittering(target, form, visTraadContainer, traadContainer, svarContainer, leggTilbakePanel);
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        return form;
    }

    private SvarOgReferatVM lagModelObjectMedKanalOgTemagruppe() {
        SvarOgReferatVM svarOgReferatVM = new SvarOgReferatVM();
        svarOgReferatVM.kanal = SvarKanal.TEKST;
        svarOgReferatVM.temagruppe = getTemagruppeFraSporsmal();
        return svarOgReferatVM;
    }

    private void sendOgVisKvittering(AjaxRequestTarget target, Form<SvarOgReferatVM> form, Component... components) {
        SvarOgReferatVM svarOgReferatVM = form.getModelObject();
        sendHenvendelse(svarOgReferatVM, fnr);

        kvittering.visISekunder(Duration.seconds(3), target, getString(svarOgReferatVM.kanal.getKvitteringKey()), components);

        form.setModelObject(new SvarOgReferatVM());

        target.add(components);
    }

    private void sendHenvendelse(SvarOgReferatVM svarOgReferatVM, String fnr) {
        Svar svar = new Svar()
                .withFnr(fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withSporsmalsId(sporsmal.id)
                .withTemagruppe(svarOgReferatVM.temagruppe.name())
                .withKanal(svarOgReferatVM.kanal.name())
                .withFritekst(svarOgReferatVM.getFritekst());

        sakService.sendSvar(svar);
        sakService.ferdigstillOppgaveFraGsak(sporsmal.oppgaveId);
    }

    @RunOnEvents(LeggTilbakePanel.LEGG_TILBAKE_ABRUTT)
    public void visPanel(AjaxRequestTarget target) {
        svarContainer.setVisibilityAllowed(true);
        leggTilbakePanel.setVisibilityAllowed(false);
        target.add(this);
    }

    //Denne er midlertidig mens vi venter på full integrasjon med kodeverk
    private Temagruppe getTemagruppeFraSporsmal() {
        for (Temagruppe temagruppe : Temagruppe.values()) {
            if (temagruppe.name().equals(sporsmal.temagruppe)) {
                return temagruppe;
            }
        }
        return Temagruppe.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT; //Bruker denne som default
    }
}
