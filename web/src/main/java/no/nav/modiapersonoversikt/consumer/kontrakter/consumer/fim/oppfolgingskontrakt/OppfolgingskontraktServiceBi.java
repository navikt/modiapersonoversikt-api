package no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt;

import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.modiapersonoversikt.consumer.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktResponse;

public interface OppfolgingskontraktServiceBi {
    OppfolgingskontraktResponse hentOppfolgingskontrakter(OppfolgingskontraktRequest request);
}
