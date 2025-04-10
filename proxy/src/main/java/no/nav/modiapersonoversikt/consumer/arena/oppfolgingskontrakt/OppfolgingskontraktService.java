package no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.OppfolgingskontraktResponse;

public interface OppfolgingskontraktService {
    OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request);
}
