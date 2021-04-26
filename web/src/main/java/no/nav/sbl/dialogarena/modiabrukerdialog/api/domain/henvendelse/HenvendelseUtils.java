package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;

public class HenvendelseUtils {
    public static List<String> AKTUELLE_HENVENDELSE_TYPER = new ArrayList<>(asList(
            SPORSMAL_SKRIFTLIG.name(),
            SPORSMAL_SKRIFTLIG_DIREKTE.name(),
            SVAR_SKRIFTLIG.name(),
            SVAR_OPPMOTE.name(),
            SVAR_TELEFON.name(),
            REFERAT_OPPMOTE.name(),
            REFERAT_TELEFON.name(),
            SPORSMAL_MODIA_UTGAAENDE.name(),
            INFOMELDING_MODIA_UTGAAENDE.name(),
            SVAR_SBL_INNGAAENDE.name(),
            DOKUMENT_VARSEL.name(),
            OPPGAVE_VARSEL.name(),
            DELVIS_SVAR_SKRIFTLIG.name()
    ));
}
