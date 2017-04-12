package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.List;

public interface OrganisasjonEnhetV2Service {
    List<AnsattEnhet> hentAlleEnheter();

    Optional<AnsattEnhet> hentEnhetGittEnhetId(final String enhetId);
}
