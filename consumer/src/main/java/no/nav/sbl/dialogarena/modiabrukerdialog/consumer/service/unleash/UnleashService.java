package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash;

import no.nav.modig.modia.ping.Pingable;

public interface UnleashService extends Pingable {

    boolean isEnabled(Feature feature);

    boolean isEnabled(String feature);
}
