package no.nav.modiapersonoversikt.legacy.api.service.norg;

import no.nav.modiapersonoversikt.legacy.api.domain.norg.Ansatt;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface AnsattService {
    List<AnsattEnhet> hentEnhetsliste();
    String hentAnsattNavn(String ident);
    @NotNull Set<String> hentAnsattFagomrader(@NotNull String ident, @NotNull String enhet);
    List<Ansatt> ansatteForEnhet(AnsattEnhet enhet);
}
