package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Status.LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.FRA_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.FRA_NAV;

public class VisningUtils {

    public static String lagStatusIkonKlasse(Melding melding) {
        String meldingstype = melding.meldingstype.name().substring(0, melding.meldingstype.name().indexOf("_")).toLowerCase();
        if (FRA_BRUKER.contains(melding.meldingstype)) {
            return meldingstype;
        } else {
            return String.format("%s-%s", meldingstype, melding.status.name()).toLowerCase().replace("_", "-");
        }
    }

    public static String lagMeldingStatusTekstKey(Melding melding) {
        String key = String.format("melding.status.%s", melding.meldingstype.name());
        if (FRA_NAV.contains(melding.meldingstype)) {
            key += String.format(".%s", melding.status == LEST_AV_BRUKER ? "lest" : "ulest");
        }
        return key;
    }

}
