package no.nav.kjerneinfo.consumer.fim.behandleperson.mock;

import no.nav.tjeneste.virksomhet.behandleperson.v1.*;
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.*;

public class BehandlePersonV1Mock implements BehandlePersonV1{
    @Override
    public void ping() {

    }

    @Override
    public void opprettUtenlandskIdentitet(WSOpprettUtenlandskIdentitetRequest wsOpprettUtenlandskIdentitetRequest) throws BehandlePersonUnntak {

    }

    @Override
    public void endreNavn(WSEndreNavnRequest request) throws Sikkerhetsbegrensning, PersonIkkeFunnet, UgyldigInput, PersonIkkeUtvandret {

    }

    @Override
    public void oppdaterUtenlandskIdentitet(WSOppdaterUtenlandskIdentitetRequest wsOppdaterUtenlandskIdentitetRequest) throws BehandlePersonUnntak {

    }

    @Override
    public void opphorSikkerhetstiltak(WSOpphorSikkerhetstiltakRequest wsOpphorSikkerhetstiltakRequest) throws BehandlePersonUnntak {

    }

    @Override
    public void opphorUtenlandskIdentitet(WSOpphorUtenlandskIdentitetRequest wsOpphorUtenlandskIdentitetRequest) throws BehandlePersonUnntak {

    }

    @Override
    public void opprettSikkerhetstiltak(WSOpprettSikkerhetstiltakRequest wsOpprettSikkerhetstiltakRequest) throws BehandlePersonUnntak {

    }
}
