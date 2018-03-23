package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;

public interface LDAPService {
    Saksbehandler hentSaksbehandler(String ident);
}
