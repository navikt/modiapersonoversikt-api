package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.List;

public interface EnhetService {
    List<AnsattEnhet> hentAlleEnheter();

    AnsattEnhet hentEnhet(String enhetId);
}
