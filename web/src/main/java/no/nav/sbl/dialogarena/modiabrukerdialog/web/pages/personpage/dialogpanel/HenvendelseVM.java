package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Kanal;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Temagruppe;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static java.lang.String.format;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.Modus.SPORSMAL;

public class HenvendelseVM extends EnhancedTextAreaModel {
    public static final String SPORSMAL_KVITTERING_BEKREFTELSE = ".SPORSMAL.kvittering.bekreftelse";

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

    public String getOverskriftTekstKey(Kanal kanal) {
        String beskrivelse = "%s.beskrivelse";
        if (brukerKanSvare) {
            return format(beskrivelse, "SPORSMAL");
        } else {
            return format(beskrivelse, kanal);
        }
    }

    public String getKvitteringsTekstKeyBasertPaaBrukerKanSvare(String prefix) {
        if (brukerKanSvare) {
            return prefix + SPORSMAL_KVITTERING_BEKREFTELSE;
        } else {
            return kanal.getKvitteringKey(prefix);
        }
    }

    public String getKvitteringsTekstKeyBasertPaaModus(String prefix) {
        if (modus.equals(SPORSMAL)) {
            return prefix + SPORSMAL_KVITTERING_BEKREFTELSE;
        } else {
            return kanal.getKvitteringKey(prefix);
        }
    }

}
