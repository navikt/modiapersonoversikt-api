package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.List;

public interface OrganisasjonEnhetService {
    List<AnsattEnhet> hentAlleEnheter();

    Optional<AnsattEnhet> hentEnhet(String enhetId);
}
