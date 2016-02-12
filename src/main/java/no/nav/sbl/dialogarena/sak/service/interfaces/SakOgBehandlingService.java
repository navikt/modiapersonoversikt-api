package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import java.util.List;

public interface SakOgBehandlingService {
    List<WSSak> hentSakerForAktor(String aktorId);
}
