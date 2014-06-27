package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status;

public class VisningUtils {

    public static String getStatusKlasse(Status status) {
        return "status " + status.name().toLowerCase().replace("_", "-");
    }

    public static String lagMeldingOverskriftKey(Melding melding) {
        return "melding.overskrift." + melding.meldingstype.name().toLowerCase();
    }

}
