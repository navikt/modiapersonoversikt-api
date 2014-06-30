package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;

public class DialogVM extends EnhancedTextAreaModel {
    public Kanal kanal;
    public Temagruppe temagruppe;

    public String getFritekst() {
        return text;
    }
}
