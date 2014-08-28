package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL;

public class VisningUtils {

    public static String getStatusKlasse(Status status) {
        return "status " + status.name().toLowerCase().replace("_", "-");
    }

    public static String lagMeldingOverskriftKey(Melding melding) {
        return "melding.overskrift." + melding.meldingstype.name().toLowerCase();
    }

    public static String lagStatusIkonKlasse(Melding melding) {
        if (melding.meldingstype == SPORSMAL) {
            return SPORSMAL.name().toLowerCase();
        } else {
            return String.format("%s-%s", melding.meldingstype.name(), melding.status.name()).toLowerCase().replace("_", "-");
        }
    }

    public static String lagMeldingStatusTekstKey(Melding melding) {
        String key;
        if (melding.meldingstype == SPORSMAL) {
            key = String.format("melding.status.%s", melding.meldingstype.name());
        } else {
            key = String.format("melding.status.%s.%s.%s", melding.meldingstype.name(), melding.kanal, melding.lest ? "lest" : "ulest");
        }
        return key.toLowerCase();
    }

}
