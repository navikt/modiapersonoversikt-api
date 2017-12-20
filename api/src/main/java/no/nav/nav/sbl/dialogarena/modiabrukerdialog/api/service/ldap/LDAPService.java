package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;

public interface LDAPService {
    Saksbehandler hentSaksbehandler(String ident);
}
