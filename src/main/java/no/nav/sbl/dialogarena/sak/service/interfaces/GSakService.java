package no.nav.sbl.dialogarena.sak.service.interfaces;


import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak;

public interface GSakService {

    WSSak hentSak(String sakId);
}
