package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
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
import org.joda.time.DateTime;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static org.joda.time.DateTime.now;

public class SvarPanel extends DialogPanel {

    final String meldingsId;

    public SvarPanel(String id, String fnr, String meldingsId) {
        super(id, fnr);

        this.meldingsId = meldingsId;
        Melding melding = getMelding();

        form.add(
                new Label("dato", melding.opprettetDato),
                new Label("sporsmal", melding.fritekst)
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

    private Melding getMelding() {
        Melding melding = new Melding("id", Meldingstype.SPORSMAL, DateTime.now());
        melding.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
        return melding;
    }


    @Override
    protected void sendHenvendelse(DialogVM formInput, String fnr) {
        XMLBehandlingsinformasjon info =
                new XMLBehandlingsinformasjon()
                        .withHenvendelseType(SVAR.name())
                        .withAktor(new XMLAktor().withFodselsnummer(fnr).withNavIdent(getSubjectHandler().getUid()))
                        .withOpprettetDato(now())
                        .withAvsluttetDato(now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLSvar()
                                        .withSporsmalsId(meldingsId)
                                        .withTemagruppe(formInput.tema)
                                        .withFritekst(formInput.getFritekst())));

        ws.sendHenvendelse(new WSSendHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(fnr).withAny(info));

    }
}
