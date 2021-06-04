package no.nav.modiapersonoversikt.api.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.api.domain.norg.AnsattEnhet;

import java.util.Optional;

public interface OppfolgingsenhetService {
    Optional<AnsattEnhet> hentOppfolgingsenhet(String fodselsnummer);
}
