package no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk;


import java.util.List;

public class GsakKodeverkFraFil implements GsakKodeverk {
    private final List<GsakKode.Tema> temaer;

    public GsakKodeverkFraFil(){

        this.temaer = GsakKode.Parser.parse();
    }
    @Override
    public List<GsakKode.Tema> hentTemaListe() {
        return temaer;
    }
}
