package no.nav.modiapersonoversikt.api.service.norg;

import no.nav.modiapersonoversikt.api.domain.norg.Ansatt;
import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;

import java.util.List;

public interface AnsattService {
    List<AnsattEnhet> hentEnhetsliste();
    String hentAnsattNavn(String ident);
    List<Ansatt> ansatteForEnhet(AnsattEnhet enhet);
}
