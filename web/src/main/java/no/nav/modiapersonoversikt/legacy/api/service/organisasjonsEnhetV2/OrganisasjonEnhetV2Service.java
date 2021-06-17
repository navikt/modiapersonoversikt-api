package no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2;

import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonEnhetV2Service {
    List<AnsattEnhet> hentAlleEnheter(WSOppgavebehandlerfilter oppgavebehandlerFilter);

    Optional<AnsattEnhet> hentEnhetGittEnhetId(final String enhetId, WSOppgavebehandlerfilter oppgavebehandlerFilter);

    Optional<AnsattEnhet> finnNAVKontor(final String geografiskTilknytning, final String diskresjonskode);

    enum WSOppgavebehandlerfilter {
        KUN_OPPGAVEBEHANDLERE,
        INGEN_OPPGAVEBEHANDLERE,
        UFILTRERT
    }
}
