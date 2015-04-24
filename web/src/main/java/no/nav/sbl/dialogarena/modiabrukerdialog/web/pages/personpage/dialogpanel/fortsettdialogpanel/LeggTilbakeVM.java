package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import java.io.Serializable;

public class LeggTilbakeVM implements Serializable {

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

    public String lagTemagruppeTekst() {
        return nyTemagruppe == null ? "" : nyTemagruppe.name();
    }

}
