package no.nav.nav.sbl.dialogarena.modiabrukerdialog.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;

import java.util.List;

public interface AnsattService {
    public List<AnsattEnhet> hentEnhetsliste();
    public String hentAnsattNavn(String ident);
}
