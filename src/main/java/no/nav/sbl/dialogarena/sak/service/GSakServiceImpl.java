package no.nav.sbl.dialogarena.sak.service;


import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakRequest;

import javax.inject.Inject;

public class GSakServiceImpl implements GSakService {

    @Inject
    private SakV1 sakEndpoint;

    public WSSak hentSak(String sakId) {
        try {
            return sakEndpoint.hentSak(new WSHentSakRequest().withSakId(sakId)).getSak();
        } catch (HentSakSakIkkeFunnet e) {
            throw new SystemException("Kunne ikke hente sak med sakId: " + sakId, e);
        }
    }
}
