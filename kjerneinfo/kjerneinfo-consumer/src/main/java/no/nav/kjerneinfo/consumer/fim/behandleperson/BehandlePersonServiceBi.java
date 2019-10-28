package no.nav.kjerneinfo.consumer.fim.behandleperson;

import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest;

public interface BehandlePersonServiceBi {

    WSEndreNavnResponse endreNavn(WSEndreNavnRequest endreNavnRequest) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret;

}
