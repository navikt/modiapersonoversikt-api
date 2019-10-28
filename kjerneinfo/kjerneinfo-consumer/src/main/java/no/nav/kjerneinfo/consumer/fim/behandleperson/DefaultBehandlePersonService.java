package no.nav.kjerneinfo.consumer.fim.behandleperson;

import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest;

public class DefaultBehandlePersonService implements BehandlePersonServiceBi{

    private final BehandlePersonV1 behandlePersonV1;

    public DefaultBehandlePersonService(BehandlePersonV1 behandlePersonV1) {
        this.behandlePersonV1 = behandlePersonV1;
    }

    @Override
    public WSEndreNavnResponse endreNavn(WSEndreNavnRequest endreNavnRequest) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret {
        behandlePersonV1.endreNavn(endreNavnRequest);
        return new WSEndreNavnResponse();
    }
}
