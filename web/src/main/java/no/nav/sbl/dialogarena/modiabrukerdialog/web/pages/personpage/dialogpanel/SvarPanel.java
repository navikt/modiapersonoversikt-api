package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.time.Datoformat;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class SvarPanel extends DialogPanel {

    private Sporsmal sporsmal;
    private final WebMarkupContainer sporsmalContainer, svarContainer;
    private final LeggTilbakePanel leggTilbakePanel;

    public SvarPanel(String id, String fnr, Sporsmal sporsmal) {
        super(id, fnr);
        this.sporsmal = sporsmal;

        sporsmalContainer = new WebMarkupContainer("sporsmalcontainer");
        sporsmalContainer.setOutputMarkupId(true);
        sporsmalContainer.add(
                new Label("temagruppe", new ResourceModel(sporsmal.temagruppe)),
                new Label("dato", Datoformat.kortMedTid(sporsmal.opprettetDato)),
                new URLParsingMultiLineLabel("sporsmal", sporsmal.fritekst));

        svarContainer = new WebMarkupContainer("svarcontainer");
        svarContainer.setOutputMarkupId(true);
        svarContainer.add(
                lagSvarForm(),
                new AjaxLink<Void>("leggtilbake") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        svarContainer.setVisibilityAllowed(false);
                        leggTilbakePanel.setVisibilityAllowed(true);
                        target.add(SvarPanel.this);
                    }
                });

        leggTilbakePanel = new LeggTilbakePanel("leggtilbakepanel", sporsmal);
        leggTilbakePanel.setVisibilityAllowed(false);

        add(sporsmalContainer, svarContainer, leggTilbakePanel);
    }

    private Form lagSvarForm() {
        final Form<DialogVM> form = new Form<>("dialogform", new CompoundPropertyModel<>(lagModelObjectMedDefaultKanal()));
        form.setOutputMarkupPlaceholderTag(true);

        hentTemagruppeFraSporsmalet(form);

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
                lagTekstfelt("tekstfelt", form),
                feedbackPanel
        );

        form.add(new AjaxButton("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                submit(target, form, sporsmalContainer, svarContainer, leggTilbakePanel);
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        return form;
    }

    private DialogVM lagModelObjectMedDefaultKanal() {
        DialogVM dialogVM = new DialogVM();
        dialogVM.kanal = SvarKanal.TEKST;
        return dialogVM;
    }

    private void hentTemagruppeFraSporsmalet(Form<DialogVM> form) {
        form.getModelObject().temagruppe = getTemagruppeFromSporsmal();
    }

    @Override
    protected void sendHenvendelse(DialogVM dialogVM, String fnr) {
        Svar svar = new Svar()
                .withFnr(fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withSporsmalsId(sporsmal.id)
                .withTemagruppe(dialogVM.temagruppe.name())
                .withKanal(dialogVM.kanal.name())
                .withFritekst(dialogVM.getFritekst());

        sakService.sendSvar(svar);
        sakService.ferdigstillOppgaveFraGsak(sporsmal.oppgaveId);
    }

    //Denne er midlertidig mens vi venter på full integrasjon med kodeverk
    private Temagruppe getTemagruppeFromSporsmal() {
        for (Temagruppe temagruppe : Temagruppe.values()) {
            if (temagruppe.name().equals(sporsmal.temagruppe)) {
                return temagruppe;
            }
        }
        return Temagruppe.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT; //Bruker denne som default
    }

    @RunOnEvents(LeggTilbakePanel.LEGG_TILBAKE_ABRUTT)
    public void visPanel(AjaxRequestTarget target) {
        svarContainer.setVisibilityAllowed(true);
        leggTilbakePanel.setVisibilityAllowed(false);
        target.add(this);
    }
}
