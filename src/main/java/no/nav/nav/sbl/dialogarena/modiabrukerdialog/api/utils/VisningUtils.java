package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;

public class VisningUtils {
    public static final List<Meldingstype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE);
    public static final List<Meldingstype> FRA_NAV = asList(SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON, SPORSMAL_MODIA_UTGAAENDE);
    public static final List<Meldingstype> SPORSMAL = asList(SPORSMAL_SKRIFTLIG, SPORSMAL_MODIA_UTGAAENDE);

    public static String lagStatusIkonKlasse(Melding melding) {
        String meldingstype = melding.meldingstype.name().substring(0, melding.meldingstype.name().indexOf("_")).toLowerCase();
        if (FRA_BRUKER.contains(melding.meldingstype)) {
            return meldingstype;
        } else {
            return String.format("%s-%s", meldingstype, melding.status.name()).toLowerCase().replace("_", "-");
        }
    }

    public static String lagMeldingStatusTekstKey(Melding melding) {
        return String.format("melding.status.%s", melding.meldingstype.name());
    }

}
