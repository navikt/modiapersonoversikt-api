package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;

import javax.servlet.http.HttpServletRequest;

public interface OppfolgingsinfoApiService {

    Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, HttpServletRequest request);

}
