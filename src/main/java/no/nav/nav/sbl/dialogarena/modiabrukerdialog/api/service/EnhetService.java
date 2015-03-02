package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;

import java.util.List;

public interface EnhetService {
    List<AnsattEnhet> hentAlleEnheter();

    AnsattEnhet hentEnhet(String enhetId);
}
