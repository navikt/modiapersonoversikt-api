package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import java.util.List;

public interface Filter {
    String OPPRETTET = "opprettet";
    String AVBRUTT = "avbrutt";
    String AVSLUTTET = "avsluttet";

    List<GenerellBehandling> filtrerBehandlinger(List<GenerellBehandling> behandlinger);

    List<WSSak> filtrerSaker(List<WSSak> saker);
}
