package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;

public class HenvendelseVM extends EnhancedTextAreaModel {
    public Kanal kanal;
    public Temagruppe temagruppe;
    public Modus modus;
    public Sak valgtSak;

    public String getFritekst() {
        return text;
    }

    public static enum Modus {
        REFERAT, SPORSMAL
    }
}
