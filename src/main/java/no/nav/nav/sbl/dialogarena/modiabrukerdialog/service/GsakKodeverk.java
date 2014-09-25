package no.nav.nav.sbl.dialogarena.modiabrukerdialog.service;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.GsakKodeTema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GsakKodeverk extends Serializable {
    List<GsakKodeTema.Tema> hentTemaListe();

    Map<String, String> hentFagsystemMapping();
}
