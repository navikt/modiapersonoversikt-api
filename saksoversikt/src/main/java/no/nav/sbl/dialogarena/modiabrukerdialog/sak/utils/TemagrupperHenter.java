package no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils;

import no.nav.sbl.util.EnvironmentUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TemagrupperHenter {

    public Map<String, List<String>> genererTemagrupperMedTema() {
        String temagrupperStreng = EnvironmentUtils.getRequiredProperty("saksoversikt.temagrupper");

        return kommaseparertTekstTilListe(temagrupperStreng).stream()
                .collect(Collectors.toMap(Function.identity(), temagruppe -> hentTemaForGruppe(temagruppe)));
    }

    private static List<String> hentTemaForGruppe(String gruppe) {
        String tema = EnvironmentUtils.getRequiredProperty("saksoversikt.temagrupper." + gruppe + ".temaer");

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
