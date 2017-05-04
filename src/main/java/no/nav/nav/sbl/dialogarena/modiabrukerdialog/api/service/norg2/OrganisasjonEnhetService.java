package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg2;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;

import java.util.List;

public interface OrganisasjonEnhetService {
    List<Arbeidsfordeling> hentArbeidsfordeling(final String enhetId);
}
