package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import java.util.*;

public class AnsattEnhetUtil {


    private static final String NAV_VAERNES = "1783";
    private static final String NAV_SELBU = "1664";
    private static final String NAV_TYDAL = "1665";
    private static final String NAV_MERAAKER = "1711";
    private static final String NAV_SJOERDAL = "1714";
    private static final String NAV_FROSTA = "1717";
    public static final String PREFIX_NORD_TROENDELAG = "17";

    public static Set<String> hentEnheterForValgtEnhet(String valgtEnhet) {
        List<String> enheter = new ArrayList<>(Arrays.asList(valgtEnhet));
        if (NAV_VAERNES.equals(valgtEnhet)) {
            enheter.add(NAV_SELBU);
            enheter.add(NAV_TYDAL);
            enheter.add(NAV_MERAAKER);
            enheter.add(NAV_SJOERDAL);
            enheter.add(NAV_FROSTA);
        }
        return new HashSet<>(enheter);
    }

    public static Set<String> hentEkstraEnheterForFylke(String valgtEnhet) {
        List<String> enheter = new ArrayList<>();
        if (valgtEnhet.startsWith(PREFIX_NORD_TROENDELAG)) {
            enheter.add(NAV_SELBU);
            enheter.add(NAV_TYDAL);
            enheter.add(NAV_MERAAKER);
            enheter.add(NAV_SJOERDAL);
            enheter.add(NAV_FROSTA);
        }
        return new HashSet<>(enheter);
    }
}
