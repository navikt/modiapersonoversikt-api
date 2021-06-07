package no.nav.modiapersonoversikt.legacy.api.service.norg;

import no.nav.modiapersonoversikt.legacy.api.domain.norg.Ansatt;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;

import java.util.List;

public interface AnsattService {
    List<AnsattEnhet> hentEnhetsliste();
    String hentAnsattNavn(String ident);
    List<Ansatt> ansatteForEnhet(AnsattEnhet enhet);
}
