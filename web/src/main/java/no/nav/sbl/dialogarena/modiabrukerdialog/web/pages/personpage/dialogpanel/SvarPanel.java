package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static org.joda.time.DateTime.now;

public class SvarPanel extends DialogPanel {

    @Inject
    private SakService sakService;

    private Melding sporsmalsMelding;

    public SvarPanel(String id, String fnr, String sporsmalsId) {
        super(id, fnr);

        sporsmalsMelding = sakService.getSakFromHenvendelse(sporsmalsId);
        sakService.hentOppgaveFraGsak(sporsmalsId);

        form.getModelObject().tema = sporsmalsMelding.tema;

        form.add(
                new Label("dato", sporsmalsMelding.opprettetDato),
                new Label("sporsmal", sporsmalsMelding.fritekst)
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
        XMLBehandlingsinformasjon info =
                new XMLBehandlingsinformasjon()
                        .withHenvendelseType(SVAR.name())
                        .withAktor(new XMLAktor().withFodselsnummer(fnr).withNavIdent(getSubjectHandler().getUid()))
                        .withOpprettetDato(now())
                        .withAvsluttetDato(now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLSvar()
                                        .withSporsmalsId(sporsmalsMelding.id)
                                        .withTemagruppe(dialogVM.tema)
                                        .withFritekst(dialogVM.getFritekst())
                        ));

        ws.sendHenvendelse(new WSSendHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(fnr).withAny(info));

        sakService.ferdigstillOppgaveFraGsak(sporsmalsMelding.id);
    }
}
