package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo.Oppfolgingsinfo;

import java.util.Optional;

public interface OppfolgingsinfoService {
    Optional<Oppfolgingsinfo> hentOppfolgingsinfo(String fodselsnummer);
}
