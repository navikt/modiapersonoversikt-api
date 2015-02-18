package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap;

import no.nav.modig.lang.option.Optional;

import javax.naming.directory.Attributes;

public interface LDAPService {
    Optional<Attributes> hentSaksbehandler(String ident);
}
