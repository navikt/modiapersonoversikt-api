package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.GsakKodeverk;

import java.util.List;
import java.util.Map;

public class GsakKodeverkFraFil implements GsakKodeverk {
    private final List<GsakKodeTema.Tema> temaer;
    private final Map<String, String> fagsystemMapping;

    public GsakKodeverkFraFil() {
        this.temaer = GsakKodeverkTema.Parser.parse();
        this.fagsystemMapping = GsakKodeverkFagsystem.Parser.parse();
    }

    @Override
    public List<GsakKodeTema.Tema> hentTemaListe() {
        return temaer;
    }

    @Override
    public Map<String, String> hentFagsystemMapping() {
        return fagsystemMapping;
    }

}
