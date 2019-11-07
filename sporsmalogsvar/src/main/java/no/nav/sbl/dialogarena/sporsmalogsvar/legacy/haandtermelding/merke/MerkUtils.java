package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MerkUtils {
    public static boolean kanHastekassere(String ident) {
        if (personerMedTilgangTilHastekassering() == null) {
            return false;
        }

        List<String> personer = Stream.of(personerMedTilgangTilHastekassering().split(",", -1))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        return personer.contains(ident.toUpperCase());
    }

    private static String personerMedTilgangTilHastekassering() {
        return System.getProperty("HASTEKASSERING_TILGANG");
    }
}
