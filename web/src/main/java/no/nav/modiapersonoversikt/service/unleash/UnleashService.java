package no.nav.modiapersonoversikt.service.unleash;

import no.nav.modiapersonoversikt.infrastructure.types.Pingable;

public interface UnleashService extends Pingable {

    boolean isEnabled(Feature feature);

    boolean isEnabled(String feature);
}
