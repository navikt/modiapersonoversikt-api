package no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;

import java.util.Optional;

public interface OppfolgingsenhetService {
    Optional<AnsattEnhet> hentOppfolgingsenhet(String fodselsnummer);
}
