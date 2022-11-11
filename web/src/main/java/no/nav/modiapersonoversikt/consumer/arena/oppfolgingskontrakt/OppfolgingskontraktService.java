package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse;

public interface OppfolgingskontraktService {
    OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request);
}
