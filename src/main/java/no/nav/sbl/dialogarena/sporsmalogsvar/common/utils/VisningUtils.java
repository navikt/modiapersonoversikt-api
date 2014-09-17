package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;

public class VisningUtils {

    public static String getStatusKlasse(Status status) {
        return "status " + status.name().toLowerCase().replace("_", "-");
    }

    public static String lagMeldingOverskriftKey(Melding melding) {
        return "melding.overskrift." + hentForsteDelAvMeldingstype(melding.meldingstype);
    }

    public static String lagStatusIkonKlasse(Melding melding) {
        String meldingstype = hentForsteDelAvMeldingstype(melding.meldingstype);
        if (melding.meldingstype == SPORSMAL_SKRIFTLIG) {
            return meldingstype;
        } else {
            return String.format("%s-%s", meldingstype, melding.status.name()).toLowerCase().replace("_", "-");
        }
    }

    public static String lagMeldingStatusTekstKey(Melding melding) {
        String key;
        String meldingstypeIkkeSpesifik = hentForsteDelAvMeldingstype(melding.meldingstype);
        if (melding.meldingstype == SPORSMAL_SKRIFTLIG) {
            key = String.format("melding.status.%s", meldingstypeIkkeSpesifik);
        } else {
            key = String.format("melding.status.%s.%s.%s", meldingstypeIkkeSpesifik, melding.kanal, melding.lest ? "lest" : "ulest");
        }
        return key.toLowerCase();
    }

    private static String hentForsteDelAvMeldingstype(Meldingstype meldingstype) {
        return meldingstype.name().substring(0, meldingstype.name().indexOf("_")).toLowerCase();
    }

}
