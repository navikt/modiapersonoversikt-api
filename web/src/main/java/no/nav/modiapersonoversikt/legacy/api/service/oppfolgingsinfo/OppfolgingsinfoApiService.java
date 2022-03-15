package no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo;

import no.nav.modiapersonoversikt.consumer.ldap.LDAPService;
import no.nav.modiapersonoversikt.legacy.api.domain.oppfolgingsinfo.Oppfolgingsinfo;

import java.io.IOException;

public interface OppfolgingsinfoApiService {

    Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService);

    void ping() throws IOException;
}
