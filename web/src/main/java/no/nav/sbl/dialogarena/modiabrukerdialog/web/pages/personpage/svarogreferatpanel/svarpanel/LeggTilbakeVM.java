package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.SaksbehandlerInnstillingerService;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class LeggTilbakeVM implements Serializable {

    public static final String LINJESKILLER = "\n";

    public Aarsak valgtAarsak;
    public Temagruppe nyTemagruppe;
    public String annenAarsakTekst;

    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public LeggTilbakeVM(SaksbehandlerInnstillingerService saksbehandlerInnstillingerService) {
        this.saksbehandlerInnstillingerService = saksbehandlerInnstillingerService;
    }

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

    public String lagBeskrivelse(String beskrivelseStart, DateTime now) {
        String navident = getSubjectHandler().getUid();
        String saksbehandlerValgtEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();

        StringBuilder beskrivelseBuilder = new StringBuilder();
        beskrivelseBuilder.append("- " + formaterTimestamp(now) + " (" + navident + ", " + saksbehandlerValgtEnhet + ") -");
        beskrivelseBuilder.append(LINJESKILLER);
        beskrivelseBuilder.append((beskrivelseStart + " " + (annenAarsakTekst == null ? "" : annenAarsakTekst)).trim());

        return beskrivelseBuilder.toString();
    }

    public static String formaterTimestamp(DateTime dateTime) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("nb", "no")).format(dateTime.toDate());
    }

    public String lagTemagruppeTekst() {
        return nyTemagruppe == null ? "" : nyTemagruppe.name();
    }

    public AbstractReadOnlyModel<Boolean> erValgtAarsak(final Aarsak aarsak) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return aarsak.equals(valgtAarsak);
            }
        };
    }

}
