package no.nav.sbl.dialogarena.sak.service;


import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;

public interface GSakService {

    WSSak hentSak(String sakId);
}
