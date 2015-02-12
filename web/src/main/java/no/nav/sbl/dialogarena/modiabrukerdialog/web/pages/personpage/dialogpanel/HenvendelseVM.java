package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class HenvendelseVM extends EnhancedTextAreaModel {
    public Kanal kanal;
    public Temagruppe temagruppe;
    public Modus modus;
    public Sak valgtSak;
    public boolean brukerKanSvare = false;

    public String getFritekst() {
        return text;
    }

    public static enum Modus {
        REFERAT, SPORSMAL
    }

    public AbstractReadOnlyModel<Boolean> sakErSatt() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return valgtSak != null;
            }
        };
    }

    public AbstractReadOnlyModel<Boolean> brukerKanSvareSkalEnables() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return kanal.equals(Kanal.TEKST);
            }
        };
    }

    public String getValgtSaksDatoFormatert() {
        return valgtSak.opprettetDato == null ? "" : WidgetDateFormatter.date(valgtSak.opprettetDato);
    }
}
