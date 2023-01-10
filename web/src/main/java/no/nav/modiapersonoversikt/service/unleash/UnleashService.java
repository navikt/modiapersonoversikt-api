package no.nav.modiapersonoversikt.service.unleash;

import no.nav.modiapersonoversikt.infrastructure.ping.Pingable;

public interface UnleashService extends Pingable {

    boolean isEnabled(Feature feature);

    boolean isEnabled(String feature);
}
