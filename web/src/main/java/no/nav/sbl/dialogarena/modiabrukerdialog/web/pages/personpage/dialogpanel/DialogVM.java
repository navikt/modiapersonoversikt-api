package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.Kanal.TELEFON;

public class DialogVM extends EnhancedTextAreaModel {
    public Kanal kanal = TELEFON;
    public String tema;

    public String getFritekst() {
        return text;
    }
}
