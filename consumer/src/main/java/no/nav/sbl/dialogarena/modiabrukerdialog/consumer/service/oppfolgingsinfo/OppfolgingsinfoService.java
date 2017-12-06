package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.domain.Oppfolgingsinfo;

public interface OppfolgingsinfoService {
    Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer);
}
