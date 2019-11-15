package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.Optional;

public interface OppfolgingsenhetService {
    public Optional<AnsattEnhet> hentOppfolgingsenhet(String fodselsnummer);
}
