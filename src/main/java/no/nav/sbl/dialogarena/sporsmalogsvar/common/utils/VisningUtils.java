package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;

import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.LEST_AV_BRUKER;

public class VisningUtils {

    public static String lagStatusIkonKlasse(Melding melding) {
        String meldingstype = melding.meldingstype.name().substring(0, melding.meldingstype.name().indexOf("_")).toLowerCase();
        if (melding.meldingstype == SPORSMAL_SKRIFTLIG) {
            return meldingstype;
        } else {
            return String.format("%s-%s", meldingstype, melding.status.name()).toLowerCase().replace("_", "-");
        }
    }

    public static String lagMeldingStatusTekstKey(Melding melding) {
        String key = String.format("melding.status.%s", melding.meldingstype.name());
        if (melding.meldingstype != SPORSMAL_SKRIFTLIG) {
            key += String.format(".%s", melding.status == LEST_AV_BRUKER ? "lest" : "ulest");
        }
        return key;
    }

}
