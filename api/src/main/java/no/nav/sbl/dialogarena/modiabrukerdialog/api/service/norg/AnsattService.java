package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Ansatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.List;

public interface AnsattService {
    List<AnsattEnhet> hentEnhetsliste();
    String hentAnsattNavn(String ident);
    List<Ansatt> ansatteForEnhet(AnsattEnhet enhet);
}
