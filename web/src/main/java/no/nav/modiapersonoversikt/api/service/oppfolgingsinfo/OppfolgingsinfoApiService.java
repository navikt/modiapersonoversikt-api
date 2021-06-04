package no.nav.modiapersonoversikt.api.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.modiapersonoversikt.api.service.ldap.LDAPService;

import java.io.IOException;

public interface OppfolgingsinfoApiService {

    Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService);

    void ping() throws IOException;
}
