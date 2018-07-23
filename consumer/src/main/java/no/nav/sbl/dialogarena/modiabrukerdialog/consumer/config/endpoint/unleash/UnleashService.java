package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature;

public interface UnleashService extends Pingable {

    boolean isEnabled(Feature feature);
}
