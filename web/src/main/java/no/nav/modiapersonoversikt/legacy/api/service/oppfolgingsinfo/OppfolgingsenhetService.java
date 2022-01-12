package no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.consumer.norg.NorgDomain;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;

import java.util.Optional;

public interface OppfolgingsenhetService {
    Optional<NorgDomain.Enhet> hentOppfolgingsenhet(String fodselsnummer);
}
