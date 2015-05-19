package no.nav.sbl.dialogarena.sak.service;


import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakRequest;

import javax.inject.Inject;

public class GSakServiceImpl implements GSakService {

    @Inject
    private SakV1 sakEndpoint;

    public WSSak hentSak(String sakId) throws HentSakSakIkkeFunnet {
        return sakEndpoint.hentSak(new WSHentSakRequest().withSakId(sakId)).getSak();
    }
}
