package no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling;

import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.EnhetsGeografiskeTilknytning;

import java.util.List;

public interface ArbeidsfordelingV1Service {
    List<AnsattEnhet> finnBehandlendeEnhetListe(String brukerIdent, String fagomrade, String oppgavetype, String underkategori);

    List<EnhetsGeografiskeTilknytning> hentGTnummerForEnhet(String valgtEnhet);
}
