package no.nav.kjerneinfo.consumer.fim.behandleperson.mock;

import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest;

public class BehandlePersonV1Mock implements BehandlePersonV1{
    @Override
    public void ping() {

    }

    @Override
    public void endreNavn(WSEndreNavnRequest request) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret {

    }
}
