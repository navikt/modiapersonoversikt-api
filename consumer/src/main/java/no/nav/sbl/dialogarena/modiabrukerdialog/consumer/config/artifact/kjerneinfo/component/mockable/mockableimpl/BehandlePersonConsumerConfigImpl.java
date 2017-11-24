package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi;
import no.nav.kjerneinfo.consumer.fim.behandleperson.DefaultBehandlePersonService;
import no.nav.tjeneste.virksomhet.behandleperson.v1.BehandlePersonV1;

public class BehandlePersonConsumerConfigImpl {

    private BehandlePersonV1 behandlePersonV1;

    public BehandlePersonConsumerConfigImpl(BehandlePersonV1 behandlePersonV1) {
        this.behandlePersonV1 = behandlePersonV1;
    }

    public BehandlePersonServiceBi behandlePersonV1() {
        return new DefaultBehandlePersonService(behandlePersonV1);
    }
}
