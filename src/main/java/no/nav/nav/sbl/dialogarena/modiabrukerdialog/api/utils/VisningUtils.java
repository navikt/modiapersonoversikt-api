package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;

public class VisningUtils {
    public static final List<Meldingstype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE);
    public static final List<Meldingstype> FRA_NAV = asList(SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON, SPORSMAL_MODIA_UTGAAENDE);
    public static final List<Meldingstype> SPORSMAL = asList(SPORSMAL_SKRIFTLIG, SPORSMAL_MODIA_UTGAAENDE);

    public static String lagStatusIkonKlasse(Melding melding) {
        return String.format("statusIkon %s", melding.status == Status.IKKE_BESVART ? "ubesvart" : "besvart");
    }

    public static String lagMeldingStatusTekstKey(Melding melding) {
        return String.format("melding.status.%s", melding.meldingstype.name());
    }

}
