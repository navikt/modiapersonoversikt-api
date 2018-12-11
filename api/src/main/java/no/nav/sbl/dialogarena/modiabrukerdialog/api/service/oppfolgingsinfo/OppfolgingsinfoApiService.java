package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;

import java.io.IOException;


public interface OppfolgingsinfoApiService {

    Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService);

    void ping()throws IOException;
}
