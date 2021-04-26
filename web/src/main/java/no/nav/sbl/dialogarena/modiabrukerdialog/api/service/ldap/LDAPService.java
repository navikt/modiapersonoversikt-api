package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;

import java.util.List;

public interface LDAPService {
    Saksbehandler hentSaksbehandler(String ident);
    List<String> hentRollerForVeileder(String ident);
    boolean saksbehandlerHarRolle(String ident, String rolle);
}
