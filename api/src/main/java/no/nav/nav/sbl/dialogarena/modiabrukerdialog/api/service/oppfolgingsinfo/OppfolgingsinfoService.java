package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;

public interface OppfolgingsinfoService {
    Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer);
}
