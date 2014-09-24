package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;


import java.util.List;
import java.util.Map;

public class GsakKodeverkFraFil implements GsakKodeverk {
    private final List<GsakKodeTema.Tema> temaer;
    private final Map<String, String> fagsystemMapping;

    public GsakKodeverkFraFil() {
        this.temaer = GsakKodeTema.Parser.parse();
        this.fagsystemMapping = GsakKodeFagsystem.Parser.parse();
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
