package no.nav.sbl.dialogarena.sak.service;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import java.util.List;

public interface SakOgBehandlingService {
    List<WSSak> hentSakerForAktor(String aktorId);
}
