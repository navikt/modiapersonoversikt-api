package no.nav.modiapersonoversikt.legacy.api.utils;

import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype.*;

public class VisningUtils {
    public static final List<Meldingstype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG_DIREKTE, SVAR_SBL_INNGAAENDE);
    public static final List<Meldingstype> FRA_NAV = asList(
            SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON,
            SPORSMAL_MODIA_UTGAAENDE, DOKUMENT_VARSEL, OPPGAVE_VARSEL, DELVIS_SVAR_SKRIFTLIG, INFOMELDING_MODIA_UTGAAENDE
    );
    public static final List<Meldingstype> VARSEL = asList(OPPGAVE_VARSEL, DOKUMENT_VARSEL);
    public static final List<Meldingstype> SPORSMAL = asList(SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG_DIREKTE, SPORSMAL_MODIA_UTGAAENDE);

    public static String lagMeldingStatusTekstKey(Meldingstype meldingstype) {
        return String.format("melding.status.%s", meldingstype.name());
    }

}
