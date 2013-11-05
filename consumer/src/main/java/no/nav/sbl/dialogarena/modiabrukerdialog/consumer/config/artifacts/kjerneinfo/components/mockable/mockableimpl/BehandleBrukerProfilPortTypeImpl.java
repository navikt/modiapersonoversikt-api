package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;

public class BehandleBrukerProfilPortTypeImpl {

    private BehandleBrukerprofilPortType behandleBrukerprofilPortType;
    private BehandleBrukerprofilPortType selfTestBehandleBrukerprofilPortType;

    public BehandleBrukerProfilPortTypeImpl(BehandleBrukerprofilPortType behandleBrukerprofilPortType, BehandleBrukerprofilPortType selfTestBehandleBrukerprofilPortType) {
        this.behandleBrukerprofilPortType = behandleBrukerprofilPortType;
        this.selfTestBehandleBrukerprofilPortType = selfTestBehandleBrukerprofilPortType;
    }

    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        return new DefaultBehandleBrukerprofilService(behandleBrukerprofilPortType, selfTestBehandleBrukerprofilPortType, new BehandleBrukerprofilMapper());
    }

}
