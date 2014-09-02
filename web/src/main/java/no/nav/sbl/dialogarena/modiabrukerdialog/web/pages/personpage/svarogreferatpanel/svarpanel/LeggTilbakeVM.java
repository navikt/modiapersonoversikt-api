package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.lang.String.format;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class LeggTilbakeVM implements Serializable {

    public static final String LINJESKILLER = "\n";

    public Aarsak valgtAarsak;
    public Temagruppe nyTemagruppe;
    public String annenAarsakTekst;

    public static enum Aarsak {
        FEIL_TEMAGRUPPE,
        INHABIL,
        ANNEN
    }

    public String getBeskrivelseKey() {
        switch (valgtAarsak) {
            case FEIL_TEMAGRUPPE:
                return "leggtilbake.beskrivelse.feiltema";
            case INHABIL:
                return "leggtilbake.beskrivelse.inhabil";
            case ANNEN:
                return "leggtilbake.beskrivelse.annenaarsak";
            default:
                return null;
        }
    }

    public String lagBeskrivelse(String beskrivelseStart, String saksbehandlerValgtEnhet) {
        String navident = getSubjectHandler().getUid();

        StringBuilder beskrivelseBuilder = new StringBuilder();
        beskrivelseBuilder.append(format("- %s (%s, %s) -", getFormatertTimestamp(), navident, saksbehandlerValgtEnhet));
        beskrivelseBuilder.append(LINJESKILLER);
        beskrivelseBuilder.append((beskrivelseStart + " " + (annenAarsakTekst == null ? "" : annenAarsakTekst)).trim());

        return beskrivelseBuilder.toString();
    }

    public static String getFormatertTimestamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("nb", "no")).format(DateTime.now().toDate());
    }

    public String lagTemagruppeTekst() {
        return nyTemagruppe == null ? "" : nyTemagruppe.name();
    }

}
