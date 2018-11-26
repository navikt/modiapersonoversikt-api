package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;

import javax.servlet.http.HttpServletRequest;

public class OppfolgingsinfoApiServiceMock implements OppfolgingsinfoApiService {
    @Override
    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer,  HttpServletRequest httpServletRequest) {
        return null;
    }
}
