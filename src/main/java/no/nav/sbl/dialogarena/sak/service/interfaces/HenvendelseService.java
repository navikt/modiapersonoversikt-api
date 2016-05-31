package no.nav.sbl.dialogarena.sak.service.interfaces;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;

import java.util.List;

public interface HenvendelseService {
    List<WSSoknad> hentInnsendteSoknader(String fnr);
}
