package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import java.util.List;
import java.util.Map;

public interface DataFletter {
    Map<TemaVM, List<GenerellBehandling>> hentBehandlingerByTema(List<WSSak> saker, List<WSSoknad> soknader);
}
