package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MerkUtils {
    private static final String PERSONER_MED_TILGANG_TIL_HASTEKASSERING = System.getProperty("hastekassering.tilgang");

    public static boolean kanHastekassere(String ident) {
        if (PERSONER_MED_TILGANG_TIL_HASTEKASSERING == null) {
            return false;
        }

        List<String> personer = Stream.of(PERSONER_MED_TILGANG_TIL_HASTEKASSERING.split(",", -1))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        return personer.contains(ident.toUpperCase());
    }
}
