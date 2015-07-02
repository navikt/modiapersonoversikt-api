package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;

public interface LDAPService {
    Person hentSaksbehandler(String ident);
}
