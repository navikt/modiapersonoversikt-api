package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GsakKodeverk extends Serializable {
    List<GsakKodeTema.Tema> hentTemaListe();

    Map<String, String> hentFagsystemMapping();
}
