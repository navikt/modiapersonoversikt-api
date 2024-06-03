package no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt;

import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest;
import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse;

public interface OppfolgingskontraktService {
    OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request);
}
