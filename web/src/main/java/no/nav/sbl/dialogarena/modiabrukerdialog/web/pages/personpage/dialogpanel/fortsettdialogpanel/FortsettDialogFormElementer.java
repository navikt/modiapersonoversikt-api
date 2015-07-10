package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.OppgaveTilknytningPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.SkrivestottePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing.JournalforingsPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.KOMMUNALE_TJENESTER;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT;

public class FortsettDialogFormElementer extends WebMarkupContainer {

    private final SkrivestottePanel skrivestottePanel;

    public FortsettDialogFormElementer(String id, GrunnInfo grunnInfo, final IModel<HenvendelseVM> model) {
        super(id, model);

        final List<Component> avhengerAvKanlOgDelMedBrukerValg = new ArrayList<>();

        EnhancedTextArea tekstfelt = new EnhancedTextArea("tekstfelt", model,
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(150)
                        .withPlaceholderTextKey("fortsettdialogform.tekstfelt.placeholder")
        );
        add(tekstfelt);

        skrivestottePanel = new SkrivestottePanel("skrivestotteContainer", grunnInfo, tekstfelt);
        add(skrivestottePanel);

        add(new AjaxLink("skrivestotteToggler") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                skrivestottePanel.vis(target);
            }
        });

        final RadioGroup<Kanal> kanalRadioGroup = new RadioGroup<>("kanal");
        kanalRadioGroup.setRequired(true);
        kanalRadioGroup.add(new ListView<Kanal>("kanalvalg", asList(Kanal.values())) {
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
        add(kanalRadioGroup);

        final CheckBox brukerKanSvare = new CheckBox("brukerKanSvare");
        brukerKanSvare
                .setOutputMarkupId(true)
                .add(enabledIf(model.getObject().brukerKanSvareSkalEnables()));
        add(brukerKanSvare);

        OppgaveTilknytningPanel oppgaveTilknytningPanel = new OppgaveTilknytningPanel("oppgaveTilknytningPanel", model, grunnInfo, new PropertyModel<Boolean>(model, "brukerKanSvare"));
        add(oppgaveTilknytningPanel);

        final Label kanalbeskrivelse = new Label("kanalbeskrivelse", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return getString(model.getObject().getOverskriftTekstKey(kanalRadioGroup.getModelObject()));
            }
        });
        kanalbeskrivelse.setOutputMarkupId(true);
        add(kanalbeskrivelse);

        JournalforingsPanel journalforingsPanel = new JournalforingsPanel("journalforing", grunnInfo.bruker.fnr, model);
        journalforingsPanel.add(visibleIf(
                both(brukerKanSvare.getModel())
                        .and(not(model.getObject().traadJournalfort()))
                        .and(not(Model.of(KOMMUNALE_TJENESTER.contains(model.getObject().gjeldendeTemagruppe))))));
        add(journalforingsPanel);

        avhengerAvKanlOgDelMedBrukerValg.add(kanalbeskrivelse);
        avhengerAvKanlOgDelMedBrukerValg.add(brukerKanSvare);
        avhengerAvKanlOgDelMedBrukerValg.add(journalforingsPanel);
        avhengerAvKanlOgDelMedBrukerValg.add(oppgaveTilknytningPanel);

        brukerKanSvare.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                oppdaterAlleElementerSomAvhengerAvKanalOgDelMedBrukerValg(target, avhengerAvKanlOgDelMedBrukerValg);
            }
        });
        kanalRadioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                settDelMedBrukerTilFalseDersomDenneSkalDisables(model, brukerKanSvare);
                oppdaterAlleElementerSomAvhengerAvKanalOgDelMedBrukerValg(target, avhengerAvKanlOgDelMedBrukerValg);
            }
        });
    }

    private void oppdaterAlleElementerSomAvhengerAvKanalOgDelMedBrukerValg(AjaxRequestTarget target, List<Component> avhengerAvKanlOgDelMedBrukerValg) {
        for (Component component : avhengerAvKanlOgDelMedBrukerValg) {
            target.add(component);
        }
    }

    private void settDelMedBrukerTilFalseDersomDenneSkalDisables(IModel<HenvendelseVM> model, CheckBox brukerKanSvare) {
        if (!model.getObject().brukerKanSvareSkalEnables().getObject()) {
            brukerKanSvare.getModel().setObject(false);
            brukerKanSvare.modelChanged();
        }
    }

    @RunOnEvents(SAKSBEHANDLERINNSTILLINGER_VALGT)
    public void oppdaterReferatVM(AjaxRequestTarget target) {
        skrivestottePanel.oppdater(target);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#" + get("tekstfelt:text").getMarkupId() + "').focus();"));
    }
}
