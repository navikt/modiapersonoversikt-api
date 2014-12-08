package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal;

public class SvarOgReferatVM extends EnhancedTextAreaModel {
    public Kanal kanal;
    public Temagruppe temagruppe;

    public String getFritekst() {
        return text;
    }
}
