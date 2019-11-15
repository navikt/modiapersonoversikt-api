package no.nav.kjerneinfo.consumer.fim.behandleperson.mock;

import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi;
import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest;

public class BehandlePersonServiceBiMock implements BehandlePersonServiceBi {

    @Override
    public WSEndreNavnResponse endreNavn(WSEndreNavnRequest endreNavnRequest) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret {
        return new WSEndreNavnResponse();
    }
}
