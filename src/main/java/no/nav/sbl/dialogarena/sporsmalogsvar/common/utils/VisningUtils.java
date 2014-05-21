package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.INNGAENDE;

public class VisningUtils {

    public static String getStatusKlasse(Status status) {
        return "status " + status.name().toLowerCase().replace("_", "-");
    }

    public static String lagMeldingOverskrift(List<Melding> traad, Melding melding) {
        return (melding.equals(on(traad).collect(ELDSTE_FORST).get(0)) ? "Melding" : "Svar") + " fra " +
                (melding.meldingstype == INNGAENDE ? "Bruker" : "NAV");
    }
}
