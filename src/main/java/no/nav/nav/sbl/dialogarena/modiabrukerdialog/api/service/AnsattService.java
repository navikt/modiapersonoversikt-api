package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Ansatt;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;

import java.util.List;

public interface AnsattService {
    List<AnsattEnhet> hentEnhetsliste();
    String hentAnsattNavn(String ident);
    List<Ansatt> ansatteForEnhet(AnsattEnhet enhet);
}
