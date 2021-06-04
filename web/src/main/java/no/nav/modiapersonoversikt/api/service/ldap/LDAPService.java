package no.nav.modiapersonoversikt.api.service.ldap;

import no.nav.modiapersonoversikt.api.domain.Saksbehandler;

import java.util.List;

public interface LDAPService {
    Saksbehandler hentSaksbehandler(String ident);
    List<String> hentRollerForVeileder(String ident);
    boolean saksbehandlerHarRolle(String ident, String rolle);
}
