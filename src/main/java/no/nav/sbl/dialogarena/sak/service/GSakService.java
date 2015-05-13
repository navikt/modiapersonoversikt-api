package no.nav.sbl.dialogarena.sak.service;


import no.nav.tjeneste.virksomhet.sak.v1.HentSakSakIkkeFunnet;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSHentSakResponse;

public interface GSakService {

    WSHentSakResponse hentSak(String sakId) throws HentSakSakIkkeFunnet;
}
