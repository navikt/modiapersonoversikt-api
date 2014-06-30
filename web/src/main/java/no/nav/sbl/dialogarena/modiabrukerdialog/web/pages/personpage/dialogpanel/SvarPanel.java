package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class SvarPanel extends DialogPanel {

    private Sporsmal sporsmal;

    public SvarPanel(String id, String fnr, Sporsmal sporsmal) {
        super(id, fnr);
        this.sporsmal = sporsmal;

        hentTemagruppeFraSporsmalet();

        form.add(
                new Label("temagruppe", new ResourceModel(sporsmal.temagruppe)),
                new Label("dato", Datoformat.kortMedTid(sporsmal.opprettetDato)),
                new Label("navIdent", getSubjectHandler().getUid()),
                new URLParsingMultiLineLabel("sporsmal", sporsmal.fritekst)
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

    protected void hentTemagruppeFraSporsmalet() {
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

}
