package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.EnhetsGeografiskeTilknytning;

import java.util.List;

public interface ArbeidsfordelingV1Service {
    List<AnsattEnhet> finnBehandlendeEnhetListe(String brukerIdent, String fagomrade, String oppgavetype, String underkategori);

    List<EnhetsGeografiskeTilknytning> hentGTnummerForEnhet(String valgtEnhet);
}
