package no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker;


import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.GsakKodeTema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GsakKodeverk extends Serializable {

    List<GsakKodeTema.Tema> hentTemaListe();

    Map<String, String> hentFagsystemMapping();

}
