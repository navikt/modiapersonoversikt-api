package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.mdc;

import org.slf4j.MDC;

/**
 * Kontekstinformasjon for feilanalyse.
 * 
 * Putter informasjon om tråd i en egen map, slik at dette kan puttes i logg dersom tråden feiler. For personsøk er
 * informasjonen relatert til logging av tjenstekall.
 */
public final class MDCUtils {

    public static final String FNR = "fnr";
    public static final String OPERASJON = "operasjon";

    private MDCUtils() {
    }

    public static void putMDCInfo(String tjenesteKall, String id) {

        MDC.put(OPERASJON, tjenesteKall);
        MDC.put(FNR, id);
    }

    public static void clearMDCInfo() {
        MDC.remove(OPERASJON);
        MDC.remove(FNR);
    }
}
