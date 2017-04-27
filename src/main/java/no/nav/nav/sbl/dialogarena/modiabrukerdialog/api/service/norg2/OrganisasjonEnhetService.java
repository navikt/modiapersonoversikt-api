package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;

import java.util.List;

public interface OrganisasjonEnhetService {
    Optional<AnsattEnhet> hentEnhetGittGeografiskNedslagsfelt(final String geografiskNedslagsfelt);
    
    List<Arbeidsfordeling> hentArbeidsfordeling(final String enhetId);
}
