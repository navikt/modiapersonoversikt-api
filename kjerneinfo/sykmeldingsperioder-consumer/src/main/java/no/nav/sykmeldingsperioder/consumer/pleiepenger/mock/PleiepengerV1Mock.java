package no.nav.sykmeldingsperioder.consumer.pleiepenger.mock;

import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetUgyldigIdentNr;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;

public class PleiepengerV1Mock implements PleiepengerV1 {

    @Override
    public WSHentPleiepengerettighetResponse hentPleiepengerettighet(WSHentPleiepengerettighetRequest wsHentPleiepengerettighetRequest) throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        return PleiepengerMockFactory.createWsHentPleiepengerListeResponse();
    }

    @Override
    public void ping() {

    }
}
