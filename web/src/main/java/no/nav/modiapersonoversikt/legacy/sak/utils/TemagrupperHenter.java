package no.nav.modiapersonoversikt.legacy.sak.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TemagrupperHenter {

    public Map<String, List<String>> genererTemagrupperMedTema() {
        String temagrupperStreng = System.getProperty("saksoversikt.temagrupper");

        return kommaseparertTekstTilListe(temagrupperStreng).stream()
                .collect(Collectors.toMap(Function.identity(), temagruppe -> hentTemaForGruppe(temagruppe)));
    }

    private static List<String> hentTemaForGruppe(String gruppe) {
        String tema = System.getProperty("saksoversikt.temagrupper." + gruppe + ".temaer");

        return kommaseparertTekstTilListe(tema);
    }

    private static List<String> kommaseparertTekstTilListe(String tekst) {
        if (tekst != null) {
            return Arrays.asList(tekst.split(","));
        } else {
            return new ArrayList<>();
        }
    }
}
