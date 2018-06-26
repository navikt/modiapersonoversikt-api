package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.nav.modig.modia.ping.Pingable;

public interface UnleashService extends Pingable {

    boolean isEnabled(String toggleName);
}
