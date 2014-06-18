package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class ReferatPanel extends DialogPanel {

    public ReferatPanel(String id, String fnr) {
        super(id, fnr);

        form.add(new RadioGroup<ReferatKanal>("kanal")
                .setRequired(true)
                .add(new ListView<ReferatKanal>("kanalvalg", asList(ReferatKanal.values())) {
                    @Override
                    protected void populateItem(ListItem<ReferatKanal> item) {
                        item.add(new Radio<>("kanalknapp", item.getModel()));
                        item.add(new WebMarkupContainer("kanalikon").add(cssClass(item.getModelObject().name().toLowerCase())));
                    }
                }));
    }

    protected void sendHenvendelse(DialogVM dialogVM, String fnr) {
        Referat referat = new Referat()
                .withFnr(fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withTema(dialogVM.tema)
                .withKanal(dialogVM.kanal.name())
                .withFritekst(dialogVM.getFritekst());
        sakService.sendReferat(referat);
    }

}
