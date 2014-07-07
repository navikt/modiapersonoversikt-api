package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import org.apache.wicket.model.AbstractReadOnlyModel;

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

    public AbstractReadOnlyModel<Boolean> erValgtAarsak(final Aarsak aarsak) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return aarsak.equals(valgtAarsak);
            }
        };
    }
}
