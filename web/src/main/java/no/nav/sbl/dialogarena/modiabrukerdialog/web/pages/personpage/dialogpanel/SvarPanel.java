package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmaal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.StringResourceModel;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class SvarPanel extends DialogPanel {

    private Sporsmaal sporsmaal;

    public SvarPanel(String id, String fnr, String sporsmalsId) {
        super(id, fnr);

        sporsmaal = sakService.getSporsmaal(sporsmalsId);
        sakService.plukkSakIGsak(sporsmalsId);

        form.getModelObject().tema = sporsmaal.tema;

        form.add(
                new Label("dato", sporsmaal.opprettetDato),
                new Label("sporsmal", sporsmaal.fritekst)
        );

        final RadioGroup<SvarKanal> radioGroup = new RadioGroup<>("kanal");
        form.add(radioGroup
                .setRequired(true)
                .add(new ListView<SvarKanal>("kanalvalg", asList(SvarKanal.values())) {
                    @Override
                    protected void populateItem(ListItem<SvarKanal> item) {
                        item.add(new Radio<>("kanalknapp", item.getModel()));
                        item.add(new WebMarkupContainer("kanalikon").add(cssClass(item.getModelObject().name().toLowerCase())));
                    }
                }));
        radioGroup.setDefaultModelObject(SvarKanal.TEKST);

        final Label kanalbeskrivelse = new Label("kanalbeskrivelse", new StringResourceModel("${name}.beskrivelse", radioGroup.getModel()));
        kanalbeskrivelse.setOutputMarkupId(true);
        form.add(kanalbeskrivelse);

        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(kanalbeskrivelse);
            }
        });
    }

    @Override
    protected void sendHenvendelse(DialogVM dialogVM, String fnr) {
        Svar svar = new Svar()
                .withFnr(fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withSporsmalsId(sporsmaal.id)
                .withTema(dialogVM.tema)
                .withFritekst(dialogVM.getFritekst());

        sakService.sendSvar(svar);
        sakService.ferdigstillSakIGsak(sporsmaal);
    }

}
