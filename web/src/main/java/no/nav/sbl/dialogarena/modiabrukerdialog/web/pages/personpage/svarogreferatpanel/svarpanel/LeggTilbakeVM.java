package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.injection.Injector;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LeggTilbakeVM implements Serializable {

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public LeggTilbakeVM() {
        Injector.get().inject(this);
    }

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

    public String lagBeskrivelse(String beskrivelseStart) {
        return (beskrivelseStart + " " + (annenAarsakTekst == null ? "" : annenAarsakTekst)).trim();
    }

    public static String getFormatertTimestamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("nb", "no")).format(DateTime.now().toDate());
    }

    public String lagTemagruppeTekst() {
        return nyTemagruppe == null ? "" : nyTemagruppe.name();
    }

}
