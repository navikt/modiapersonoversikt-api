package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonEnhetV2Service {
    List<AnsattEnhet> hentAlleEnheter();

    Optional<AnsattEnhet> hentEnhetGittEnhetId(final String enhetId);

    Optional<AnsattEnhet> finnNAVKontor(final String geografiskTilknytning, final String diskresjonskode);
}
