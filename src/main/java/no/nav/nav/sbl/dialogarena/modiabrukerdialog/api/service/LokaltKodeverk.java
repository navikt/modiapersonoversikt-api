package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import java.util.List;
import java.util.Map;

public interface LokaltKodeverk {

    public Map<String, List<String>> hentTemagruppeTemaMapping();
    public Map<String, String> hentTemaTemagruppeMapping();
    public String hentTemagruppeForTema(String tema);

}
