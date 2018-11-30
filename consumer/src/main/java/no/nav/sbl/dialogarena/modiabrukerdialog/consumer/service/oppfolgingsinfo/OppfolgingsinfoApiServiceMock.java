package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;

public class OppfolgingsinfoApiServiceMock implements OppfolgingsinfoApiService {
    @Override
    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService) {
        return null;
    }
}
