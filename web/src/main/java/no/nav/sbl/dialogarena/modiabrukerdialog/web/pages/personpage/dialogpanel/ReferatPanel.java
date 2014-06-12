package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static org.joda.time.DateTime.now;

public class ReferatPanel extends DialogPanel {

    public ReferatPanel(String id, String fnr) {
        super(id, fnr);

        form.add(new RadioGroup<ReferatKanal>("kanal")
                .setRequired(true)
                .add(new ListView<ReferatKanal>("kanalvalg", asList(ReferatKanal.values())) {
                    @Override
                    protected void populateItem(ListItem<ReferatKanal> item) {
                        item.add(new Radio<>("kanalknapp", item.getModel()));
                        item.add(new WebMarkupContainer("kanalikon")).add(cssClass(item.getModelObject().name().toLowerCase()));
                    }
                }));
    }

    protected void sendHenvendelse(DialogVM formInput, String fnr) {
        XMLBehandlingsinformasjon info =
                new XMLBehandlingsinformasjon()
                        .withHenvendelseType(REFERAT.name())
                        .withAktor(new XMLAktor().withFodselsnummer(fnr).withNavIdent(getSubjectHandler().getUid()))
                        .withOpprettetDato(now())
                        .withAvsluttetDato(now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLReferat().withTemagruppe(formInput.tema).withKanal(formInput.kanal.name()).withFritekst(formInput.getFritekst())));

        ws.sendHenvendelse(new WSSendHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(fnr).withAny(info));
    }

}
