package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash;

import no.nav.sbl.dialogarena.types.Pingable;

public interface UnleashService extends Pingable {

    boolean isEnabled(Feature feature);

    boolean isEnabled(String feature);
}
